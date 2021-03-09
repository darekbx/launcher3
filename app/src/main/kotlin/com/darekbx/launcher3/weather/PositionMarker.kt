package com.darekbx.launcher3.weather

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class PositionMarker {

    companion object {
        private const val DOT_RADIUS = 3F
    }

    fun draw(x: Float, y: Float, image: Bitmap) {
        val canvas = Canvas(image)
        canvas.drawCircle(x - DOT_RADIUS, y - DOT_RADIUS, DOT_RADIUS, markerPaint)
    }

    private val markerPaint by lazy {
        Paint().apply {
            //style = Paint.Style.FILL
            isAntiAlias = true
            color = Color.RED
        }
    }
}
