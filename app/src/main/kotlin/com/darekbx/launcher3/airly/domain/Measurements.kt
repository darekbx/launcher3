package com.darekbx.launcher3.airly.domain

import com.darekbx.launcher3.format

data class Measurements(val current: Current) {

    var installationId: Int = 0
    lateinit var rateLimits: RateLimits

    val airlyIndex: Index
        get() = current.indexes.first()

    val averagePMNorm: String
        get() = "${averagePMNorm()}%"

    val humidity: String
        get() = retrieveValue("HUMIDITY")?.run { "${value.toInt()}%" } ?: ""

    val temperature: String
        get() = retrieveValue("TEMPERATURE")?.run { "${value.format(1)}°" } ?: ""

    private fun averagePMNorm(): Int {
        val pmPrefix = "PM"
        return current.standards
            .asSequence()
            .filter { it.pollutant.startsWith(pmPrefix) }
            .map { it.percent }
            .average()
            .toInt()
    }

    private fun retrieveValue(key: String) = current.values.firstOrNull { it.name == key }
}
