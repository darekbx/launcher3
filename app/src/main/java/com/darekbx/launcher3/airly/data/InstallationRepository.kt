package com.darekbx.launcher3.airly.data

class InstallationRepository(private val dataSource: InstallationDataSource) {

    suspend fun readInstallations(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double,
        maxResults: Int
    ) = dataSource.readInstallations(lat, lng, maxDistanceKm, maxResults)
}
