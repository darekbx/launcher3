package com.darekbx.launcher3.airly.domain

data class RateLimits(
    val dayLimit: Int,
    val dayRemaining: Int,
    val minuteLimit: Int,
    val minuteRemaining: Int
)
