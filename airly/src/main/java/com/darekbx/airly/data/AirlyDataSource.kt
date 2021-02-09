package com.darekbx.airly.data

import com.darekbx.airly.domain.Installation
import com.darekbx.airly.domain.Measurements

class AirlyDataSource: InstallationDataSource, MeasurementsDataSource {

    override suspend fun readInstallations(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double,
        maxResults: Int
    ): List<Installation> {

        TODO("Not yet implemented")

    }

    override suspend fun readMeasurements(installationId: Int): Measurements {

        TODO("Not yet implemented")

    }
}
