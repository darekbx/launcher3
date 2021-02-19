package com.darekbx.launcher3.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.darekbx.launcher3.BuildConfig
import com.darekbx.launcher3.LauncherApplication
import com.darekbx.launcher3.airly.data.InstallationRepository
import com.darekbx.launcher3.airly.data.MeasurementsRepository
import com.darekbx.launcher3.airly.domain.Coordinates
import com.darekbx.launcher3.airly.domain.DistanceMeasurements
import com.darekbx.launcher3.airly.domain.Installation
import com.darekbx.launcher3.format
import com.darekbx.launcher3.location.LocationProvider
import kotlinx.coroutines.Dispatchers

class AirlyViewModel(
    val installationRepository: InstallationRepository,
    val measurementsRepository: MeasurementsRepository,
    val locationProvider: LocationProvider
) : ViewModel() {

    private var currentLocation: Pair<Double, Double> = Pair(0.0, 0.0)

    val installations: LiveData<List<Installation>> =
        liveData(Dispatchers.IO) {
            currentLocation = locationProvider.currentLocation()
            val installationsWrapper = installationRepository.readInstallations(
                currentLocation.first, currentLocation.second,
                BuildConfig.AIRLY_MAX_DISTANCE, BuildConfig.AIRLY_MAX_RESULTS
            )
            when {
                installationsWrapper.value == null || installationsWrapper.hasError -> {
                    Log.e(LauncherApplication.LOG_TAG, "Unabled to load installations")
                }
                else -> emit(installationsWrapper.value)
            }
        }

    fun distanceMeasurements(installations: List<Installation>): LiveData<DistanceMeasurements> =
        Transformations.map(measurements(installations.map { it.id }), { measurement ->
            val measurementInstallation = installations.find { it.id == measurement.installationId }
            var distanceToLocation = ""
            if (measurementInstallation != null) {
                val distance = distanceToInstallation(measurementInstallation.location)
                distanceToLocation = "${(distance / 1000).format(1)}"
            }
            DistanceMeasurements(measurement, distanceToLocation)
        })

    fun measurements(installationIds: List<Int>) =
        liveData(Dispatchers.IO) {
            measurementsRepository.readMeasurements(installationIds) { measurements ->
                when {
                    measurements.value == null || measurements.hasError -> {
                        Log.e(LauncherApplication.LOG_TAG, "Unabled to load measurements")
                    }
                    else -> emit(measurements.value)
                }
            }
        }

    private fun distanceToInstallation(coordinates: Coordinates): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            coordinates.latitude, coordinates.longitude,
            currentLocation.first, currentLocation.second,
            results
        )
        return results[0].toDouble()
    }
}
