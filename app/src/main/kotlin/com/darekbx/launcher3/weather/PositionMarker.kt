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
        canvas.drawLine(0F, y - DOT_RADIUS, 1200F, y - DOT_RADIUS, linePaint)
        canvas.drawLine(x - DOT_RADIUS, 0F, x - DOT_RADIUS, 1200F, linePaint)
        canvas.drawCircle(
            x - DOT_RADIUS,
            y - DOT_RADIUS,
            DOT_RADIUS,
            markerPaint.apply { color = Color.BLACK })
        canvas.drawCircle(
            x - DOT_RADIUS,
            y - DOT_RADIUS,
            1F,
            markerPaint.apply { color = Color.WHITE })

    }

    private val markerPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = 2F
            color = Color.BLACK
        }
    }


    private val linePaint by lazy {
        Paint().apply {
            isAntiAlias = false
            strokeWidth = 1F
            color = Color.argb(41, 255, 255, 255)
        }
    }
}
