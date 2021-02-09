package com.darekbx.airly.interactions

import com.darekbx.airly.data.InstallationRepository

class ReadInstallations(private val installationRepository: InstallationRepository) {

    suspend operator fun invoke(lat: Double,
                                lng: Double,
                                maxDistanceKm: Double,
                                maxResults: Int) =
        installationRepository.readInstallations(lat, lng, maxDistanceKm, maxResults)
}
