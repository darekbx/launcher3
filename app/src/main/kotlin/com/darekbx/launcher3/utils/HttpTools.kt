package com.darekbx.launcher3.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.lang.reflect.Type

open class HttpTools {

    fun downloadString(url: String): String {
        val httpClient = provideOkHttpClient()
        val request = buildGetRequest(url.toHttpUrl())
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("HTTP ${response.code}")
        }
        return response.body?.string()
            ?: throw IOException("Response is empty")
    }

    inline fun <reified T : Any> downloadObject(url: String, type: Type): T {
        val httpClient = provideOkHttpClient()
        val request = buildGetRequest(url.toHttpUrl())
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("HTTP ${response.code}")
        }
        val responseString = response.body?.string()
            ?: throw IOException("Response is empty")
        return gson.fromJson<T>(responseString, type)
    }

    inline fun <reified T : Any> downloadObject(url: String): T {
        val httpClient = provideOkHttpClient()
        val request = buildGetRequest(url.toHttpUrl())
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("HTTP ${response.code}")
        }
        val responseString = response.body?.string()
            ?: throw IOException("Response is empty")
        return gson.fromJson<T>(responseString, object : TypeToken<T>() {}.type)
    }

    fun downloadImage(url: String): Bitmap {
        val httpClient = provideOkHttpClient()
        val request = buildGetRequest(url.toHttpUrl())
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("HTTP ${response.code}")
        }
        val responseStream = response.body?.byteStream()
            ?: throw IOException("Response is empty")
        val bitmapOptions = BitmapFactory.Options().apply { inMutable = true }
        return BitmapFactory.decodeStream(responseStream, null, bitmapOptions)
            ?: throw IOException("Unable to create bitmap")
    }

    fun provideOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        )
        .build()

    open fun buildGetRequest(httpUrl: HttpUrl) = Request.Builder()
        .url(httpUrl)
        .method("GET", null)
        .build()

    val gson by lazy { Gson() }
}
