package com.darekbx.launcher3.weather

import android.graphics.*
import com.darekbx.launcher3.utils.HttpTools
import com.darekbx.launcher3.weather.model.WeatherMap
import java.lang.IllegalStateException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RainviewerDataSource(
    private val httpTools: HttpTools
): WeatherDataSource {

    private val weatherMapsUrl by lazy { "https://api.rainviewer.com/public/weather-maps.json" }

    companion object {
        private const val ZOOM = 5
        private const val TILE_SIZE = 512
        private const val TILE_STYLE = "0_0"
        private const val MAP_STYLE = 8 // https://www.rainviewer.com/api/color-schemes.html
        private const val DOT_RADIUS = 3F
    }

    override suspend fun downloadRainPrediction(lat: Double, lng: Double): Bitmap =
        suspendCoroutine { continuation ->
            val weatherInfo = downloadWeatherInfo()
            when (val newestNowcast = weatherInfo.radar.nowcast.maxByOrNull { it.time }) {
                null -> continuation.resumeWithException(IllegalStateException("There are no newcasts"))
                else -> {
                    val weatherTile = downloadWeatherTile(
                        weatherInfo.host,
                        newestNowcast.path,
                        lat,
                        lng
                    )
                    drawCenterDot(weatherTile)
                    continuation.resume(weatherTile)
                }
            }
        }

    private fun drawCenterDot(weatherTile: Bitmap) {
        val canvas = Canvas(weatherTile)
        canvas.drawCircle(
            weatherTile.width / 2F - DOT_RADIUS,
            weatherTile.height / 2F - DOT_RADIUS,
            DOT_RADIUS,
            markerPaint
        )
    }

    private fun downloadWeatherInfo(): WeatherMap {
        return httpTools.downloadObject(weatherMapsUrl)
    }

    private fun downloadWeatherTile(host: String, nowcast: String, lat: Double, lng: Double): Bitmap {
        val url = "$host$nowcast/$TILE_SIZE/$ZOOM/$lat/$lng/$MAP_STYLE/$TILE_STYLE.png"
        return httpTools.downloadImage(url)
    }

    private val markerPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
        }
    }
}
