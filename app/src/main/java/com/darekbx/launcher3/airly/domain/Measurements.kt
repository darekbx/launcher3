package com.darekbx.launcher3.airly.domain

data class Measurements(val current: Current) {

    val airlyIndex: Index
        get() = current.indexes.first()

    val airlyIndexDescription: String
        get() {
            val index = current.indexes.first()
            return "${index.value} (${index.level})"
        }
}
