package com.darekbx.launcher3.weather

import android.graphics.Bitmap

interface WeatherDataSource {

    suspend fun downloadRainPrediction(lat:Double, lng: Double): Bitmap
}
