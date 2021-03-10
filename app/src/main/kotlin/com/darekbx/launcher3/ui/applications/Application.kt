package com.darekbx.launcher3.ui.applications

import android.graphics.drawable.Drawable

data class Application(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val position: Int
)
