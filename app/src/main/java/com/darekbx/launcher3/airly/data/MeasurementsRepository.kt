package com.darekbx.launcher3.airly.data

import com.darekbx.launcher3.airly.domain.Measurements
import com.darekbx.launcher3.airly.domain.ResponseWrapper

class MeasurementsRepository(private val dataSource: MeasurementsDataSource) {

    suspend fun readMeasurements(
        vararg installationId: Int,
        measurements: suspend (ResponseWrapper<Measurements>) -> Unit
    ) =
        dataSource.readMeasurements(*installationId, measurements = measurements)
}
