package com.darekbx.launcher3.weather

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.darekbx.launcher3.utils.HttpTools
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AntistormDataSource(
    private val httpTools: HttpTools
): WeatherDataSource {

    private annotation class AntistormName(val name: String)

    private class Paths(
        @AntistormName("nazwa_folderu")
        val dirs: List<String>,
        @AntistormName("nazwa_pliku")
        val files: List<String>,
        @AntistormName("nazwa_pliku_front")
        val filesFront: List<String>
    )

    companion object {
        private const val BASE_URL = "https://antistorm.eu"
        private const val PATH_URL = "$BASE_URL/ajaxPaths.php?lastTimestamp=0&type="
        private const val RAIN_URL = "$BASE_URL/visualPhenom/{fileFront}-radar-visualPhenomenon.png"
        private const val PROBABILITIES_URL = "$BASE_URL/archive/{dir}/{file}-radar-probabilitiesImg.png"
        private const val STORM_URL = "$BASE_URL/visualPhenom/{fileFront}-storm-visualPhenomenon.png"
        private const val TYPE_RADAR = "radar"
        private const val TYPE_STORM = "storm"
        private const val DIRS_NAME = "nazwa_folderu"
        private const val FILES_NAME = "nazwa_pliku"
        private const val FILES_FRONT_NAME = "nazwa_pliku_front"
    }

    override suspend fun downloadRainPrediction(lat: Double, lng: Double): Bitmap =
        suspendCoroutine { continuation ->
            try {
                val radarPathsString = readPaths(TYPE_RADAR)
                val radarPaths = parsePaths(radarPathsString)

                val stormPathsString = readPaths(TYPE_STORM)
                val stormPaths = parsePaths(stormPathsString)

                val rain = downloadRain(radarPaths.filesFront.first())
                val probabilities = downloadProbabilities(radarPaths.dirs.first(), radarPaths.files.first())
                val storm = downloadStorm(stormPaths.filesFront.first())

                val outImage = mergeImages(rain, probabilities, storm)
                continuation.resume(outImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun mergeImages(
        rainImage: Bitmap,
        probabilitiesImage: Bitmap,
        stormImage: Bitmap
    ): Bitmap {
        val destRect = Rect(0, 0, rainImage.width, rainImage.height)
        val outImage =
            Bitmap.createBitmap(destRect.width(), destRect.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outImage)
        canvas.drawBitmap(probabilitiesImage, null, destRect, alphaPaint)
        canvas.drawBitmap(rainImage, null, destRect, null)
        canvas.drawBitmap(stormImage, null, destRect, null)
        return outImage
    }

    private fun downloadRain(fileFront: String): Bitmap {
        val url = RAIN_URL.replace("{fileFront}", fileFront)
        return httpTools.downloadImage(url)
    }

    private fun downloadProbabilities(dir: String, file: String): Bitmap {
        val url = PROBABILITIES_URL.replace("{dir}", dir).replace("{file}", file)
        return httpTools.downloadImage(url)
    }
    
    private fun downloadStorm(fileFront: String): Bitmap {
        val url = STORM_URL.replace("{fileFront}", fileFront)
        return httpTools.downloadImage(url)
    }
    
    private fun parsePaths(paths: String): Paths {
        val lines = paths.split("<br>")
        val dirs = extractItems(lines, DIRS_NAME)
        val files = extractItems(lines, FILES_NAME)
        val filesFront = extractItems(lines, FILES_FRONT_NAME)
        return Paths(dirs, files, filesFront)
    }

    private fun extractItems(lines: List<String>, prefix: String): List<String> {
        val line = lines.find { it.startsWith(prefix) } ?: return emptyList()
        return line.removePrefix("$prefix:").split(',')
    }

    private fun readPaths(type: String): String {
        val url = "$PATH_URL$type"
        return httpTools.downloadString(url)
    }

    private val alphaPaint by lazy { Paint().apply { alpha = 80 } }
}
