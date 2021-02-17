package com.darekbx.launcher3.airly.domain

import com.darekbx.launcher3.format

data class Measurements(val current: Current) {

    val airlyIndex: Index
        get() = current.indexes.first()

    val measurementDescription: String
        get() {
            val index = current.indexes.first()
            var description = "${index.value} (${index.level})"
            description += retrieveValue("TEMPERATURE")?.run { ", ${value.format(1)}°" } ?: ""
            description += retrieveValue("HUMIDITY")?.run { ", ${value.toInt()}%" } ?: ""
            return description
        }

    private fun retrieveValue(key: String) = current.values.firstOrNull { it.name == key }
}
