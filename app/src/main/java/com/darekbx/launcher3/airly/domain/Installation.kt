package com.darekbx.launcher3.airly.domain

data class Installation(
    val id: Int,
    val location: Coordinates,
    val address: Address,
    val elevation: Double
)
