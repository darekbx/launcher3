package com.darekbx.launcher3.airly.data

import com.darekbx.launcher3.airly.domain.Installation
import com.darekbx.launcher3.airly.domain.ResponseWrapper

interface InstallationDataSource {

    suspend fun readInstallations(
        lat: Double,
        lng: Double,
        maxDistanceKm: Double,
        maxResults: Int
    ): ResponseWrapper<List<Installation>>
}
