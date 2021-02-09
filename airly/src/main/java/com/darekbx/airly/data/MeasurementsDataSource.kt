package com.darekbx.airly.data

import com.darekbx.airly.domain.Measurements

interface MeasurementsDataSource {

    suspend fun readMeasurements(installationId: Int): Measurements
}
