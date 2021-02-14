package com.darekbx.launcher3.airly.data

import com.darekbx.launcher3.airly.domain.Installation
import com.darekbx.launcher3.airly.domain.Measurements
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
import java.io.IOException
import java.lang.Exception

class AirlyDataSource(
    private val apiKey: String,
    private val httpUrlBase: HttpUrl = AIRLY_HOST.toHttpUrl()
): InstallationDataSource, MeasurementsDataSource {

    companion object {
        private const val AIRLY_HOST = "https://airapi.airly.eu"
        private const val INSTALLATIONS_ENDPOINT = "v2/installations/nearest"
        private const val MEASUREMENTS_ENDPOINT = "v2/measurements/installation"
    }

    override suspend fun readInstallations(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double,
        maxResults: Int
    ): ResponseWrapper<List<Installation>> {
        return withContext(Dispatchers.IO) {
            val httpClient = OkHttpClient()
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
                    responseJson, object: TypeToken<List<Installation>>() {}.type)
                ResponseWrapper(installations)
            } catch (e: Exception) {
                ResponseWrapper.failed<List<Installation>>()
            }
        }
    }

    override suspend fun readMeasurements(
        vararg installationId: Int,
        measurements: suspend (ResponseWrapper<Measurements>) -> Unit) {
        withContext(Dispatchers.IO) {
            val httpClient = OkHttpClient()

            for (id in installationId) {
                val httpUrl = httpUrlBase
                    .newBuilder()
                    .addPathSegments(MEASUREMENTS_ENDPOINT)
                    .addQueryParameter("installationId", "$id")
                    .build()

                val request = buildGetRequest(httpUrl)
                try {
                    val response = httpClient.newCall(request).await()
                    if (!response.isSuccessful) {
                        throw IOException("HTTP ${response.code}")
                    }
                    val responseJson = response.body?.string()
                        ?: throw IllegalStateException("Response is empty")
                    val measurement = gson.fromJson(responseJson, Measurements::class.java)
                    measurements(ResponseWrapper(measurement))
                } catch (e: Exception) {
                    measurements(ResponseWrapper.failed())
                }
            }
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
