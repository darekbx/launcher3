package com.darekbx.launcher3.airly.data

import com.darekbx.launcher3.airly.domain.Measurements
import com.darekbx.launcher3.airly.domain.ResponseWrapper

class MeasurementsRepository(private val dataSource: MeasurementsDataSource) {

    suspend fun readMeasurements(
        installationIds: List<Int>,
        measurements: suspend (ResponseWrapper<Measurements>) -> Unit
    ) =
        dataSource.readMeasurements(installationIds, measurements = measurements)
}
