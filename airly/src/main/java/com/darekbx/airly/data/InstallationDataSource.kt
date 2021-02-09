package com.darekbx.airly.data

import com.darekbx.airly.domain.Installation

interface InstallationDataSource {

    suspend fun readInstallations(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double,
        maxResults: Int
    ): List<Installation>
}
