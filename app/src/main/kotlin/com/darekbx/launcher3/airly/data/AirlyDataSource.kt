package com.darekbx.launcher3.airly.data

import com.darekbx.launcher3.airly.domain.Installation
import com.darekbx.launcher3.airly.domain.Measurements
import com.darekbx.launcher3.airly.domain.RateLimits
import com.darekbx.launcher3.airly.domain.ResponseWrapper
import com.darekbx.launcher3.await
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import java.io.IOException

class AirlyDataSource(
    private val apiKey: String,
    private val httpUrlBase: HttpUrl = AIRLY_HOST.toHttpUrl()
) : InstallationDataSource, MeasurementsDataSource {

    companion object {
        private const val AIRLY_HOST = "https://airapi.airly.eu"
        private const val INSTALLATIONS_ENDPOINT = "v2/installations/nearest"
        private const val MEASUREMENTS_ENDPOINT = "v2/measurements/installation"

        private const val X_RATELIMIT_LIMIT_DAY = "x-ratelimit-limit-day"
        private const val X_RATELIMIT_REMAINING_DAY = "x-ratelimit-remaining-day"
        private const val X_RATELIMIT_LIMIT_MINUTE = "x-ratelimit-limit-minute"
        private const val X_RATELIMIT_REMAINING_MINUTE = "x-ratelimit-remaining-minute"
    }

    override suspend fun readInstallations(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double,
        maxResults: Int
    ): ResponseWrapper<List<Installation>> {
        return withContext(Dispatchers.IO) {
            val httpClient = provideOkHttpClient()
            val httpUrl = httpUrlBase
                .newBuilder()
                .addPathSegments(INSTALLATIONS_ENDPOINT)
                .addQueryParameter("lat", "$lat")
                .addQueryParameter("lng", "$lng")
                .addQueryParameter("maxDistanceKm", "$maxDistanceKm")
                .addQueryParameter("maxResults", "$maxResults")
                .build()

            val request = buildGetRequest(httpUrl)

            try {
                val response = httpClient.newCall(request).await()
                val responseJson = response.body?.string()
                    ?: throw IllegalStateException("Response is empty")
                val installations = gson.fromJson<List<Installation>>(
                    responseJson, object : TypeToken<List<Installation>>() {}.type
                )
                ResponseWrapper(installations)
            } catch (e: IllegalStateException) {
                printDebugStackTrace(e)
                ResponseWrapper.failed()
            } catch (e: IOException) {
                printDebugStackTrace(e)
                ResponseWrapper.failed()
            }
        }
    }

    override suspend fun readMeasurements(
        installationIds: List<Int>,
        measurements: suspend (ResponseWrapper<Measurements>) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val httpClient = provideOkHttpClient()
            for (id in installationIds) {
                val value = readSingleMeasurements(id, httpClient)
                measurements(value)
            }
        }
    }

    private suspend fun readSingleMeasurements(
        id: Int,
        httpClient: OkHttpClient
    ): ResponseWrapper<Measurements> {
        val httpUrl = httpUrlBase
            .newBuilder()
            .addPathSegments(MEASUREMENTS_ENDPOINT)
            .addQueryParameter("installationId", "$id")
            .build()

        val request = buildGetRequest(httpUrl)
        return try {
            val response = httpClient.newCall(request).await()
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code}")
            }
            val rateLimits = extractRateLimits(response)
            val responseJson = response.body?.string()
                ?: throw IllegalStateException("Response is empty")
            val measurement = gson.fromJson(responseJson, Measurements::class.java)
            measurement.installationId = id
            measurement.rateLimits = rateLimits

            ResponseWrapper(measurement)
        } catch (e: IllegalStateException) {
            printDebugStackTrace(e)
            ResponseWrapper.failed()
        } catch (e: IOException) {
            printDebugStackTrace(e)
            ResponseWrapper.failed()
        }
    }

    private fun extractRateLimits(response: Response): RateLimits {
        return RateLimits(
            dayLimit = response.headers[X_RATELIMIT_LIMIT_DAY]?.toIntOrNull() ?: -1,
            dayRemaining = response.headers[X_RATELIMIT_REMAINING_DAY]?.toIntOrNull() ?: -1,
            minuteLimit = response.headers[X_RATELIMIT_LIMIT_MINUTE]?.toIntOrNull() ?: -1,
            minuteRemaining = response.headers[X_RATELIMIT_REMAINING_MINUTE]?.toIntOrNull() ?: -1
        )
    }

    private fun provideOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        )
        .build()

    private fun printDebugStackTrace(e: Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

    private fun buildGetRequest(httpUrl: HttpUrl) = Request.Builder()
        .url(httpUrl)
        .method("GET", null)
        .header("Accept", "application/json")
        .header("apikey", apiKey)
        .build()

    private val gson by lazy { Gson() }
}
