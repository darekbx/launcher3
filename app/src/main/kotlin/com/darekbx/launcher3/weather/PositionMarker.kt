package com.darekbx.launcher3.weather

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class PositionMarker {

    companion object {
        private const val DOT_RADIUS = 3F
        private const val CROSSHAIR_SIZE = 50F
    }

    fun draw(x: Float, y: Float, image: Bitmap) {
        val canvas = Canvas(image)
        drawCrosshair(canvas, x, y)
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

    private fun drawCrosshair(canvas: Canvas, x: Float, y: Float) {
        canvas.drawLine(
            x - CROSSHAIR_SIZE,
            y - DOT_RADIUS,
            x + CROSSHAIR_SIZE,
            y - DOT_RADIUS,
            linePaint
        )
        canvas.drawLine(
            x - DOT_RADIUS,
            y - CROSSHAIR_SIZE,
            x - DOT_RADIUS,
            y + CROSSHAIR_SIZE,
            linePaint
        )
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
            color = Color.argb(42, 255, 255, 255)
        }
    }
}
