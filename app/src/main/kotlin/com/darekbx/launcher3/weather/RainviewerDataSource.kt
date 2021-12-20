package com.darekbx.launcher3.weather

import android.content.SharedPreferences
import android.graphics.Bitmap
import com.darekbx.launcher3.utils.HttpTools
import com.darekbx.launcher3.utils.ScreenUtils
import com.darekbx.launcher3.weather.model.WeatherMap
import java.lang.IllegalStateException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RainviewerDataSource(
    private val httpTools: HttpTools,
    private val positionMarker: PositionMarker,
    private val sharedPreferences: SharedPreferences,
    private val screenUtils: ScreenUtils
) : WeatherDataSource {

    private val weatherMapsUrl by lazy { "https://api.rainviewer.com/public/weather-maps.json" }

    companion object {
        const val DEFAULT_ZOOM = 5
        private const val TILE_SIZE = 512
        private const val TILE_STYLE = "0_0"
        private const val MAP_STYLE = 8 // https://www.rainviewer.com/api/color-schemes.html
    }

    override suspend fun downloadRainPrediction(lat: Double, lng: Double): Bitmap =
        suspendCoroutine { continuation ->
            try {
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
                        positionMarker.draw(
                            weatherTile.width / 2F,
                            weatherTile.height / 2F,
                            weatherTile
                        )

                        val screenWidth = screenUtils.screenWidth
                        val resizedTile =
                            Bitmap.createScaledBitmap(weatherTile, screenWidth, screenWidth, false)
                        continuation.resume(resizedTile)
                    }
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }

    private fun downloadWeatherInfo(): WeatherMap {
        return httpTools.downloadObject(weatherMapsUrl)
    }

    private fun downloadWeatherTile(
        host: String,
        nowcast: String,
        lat: Double,
        lng: Double
    ): Bitmap {
        val url = "$host$nowcast/$TILE_SIZE/$zoom/$lat/$lng/$MAP_STYLE/$TILE_STYLE.png"
        return httpTools.downloadImage(url)
    }

    private val zoom: Int
        get() = sharedPreferences.getInt("rainviewer_zoom", DEFAULT_ZOOM)
}
