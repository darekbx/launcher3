package com.darekbx.airly.data

class MeasurementsRepository(private val dataSource: MeasurementsDataSource) {

    suspend fun readInstallations(installationId: Int) = dataSource.readMeasurements(installationId)
}
