package com.darekbx.launcher3.airly.data

class MeasurementsRepository(private val dataSource: MeasurementsDataSource) {

    suspend fun readMeasurements(vararg installationId: Int) =
        dataSource.readMeasurements(*installationId)
}
