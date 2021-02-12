package com.darekbx.launcher3.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.darekbx.launcher3.LauncherApplication
import com.darekbx.launcher3.airly.data.InstallationRepository
import com.darekbx.launcher3.airly.data.MeasurementsRepository
import com.darekbx.launcher3.airly.domain.Installation
import com.darekbx.launcher3.airly.domain.Measurements
import com.darekbx.launcher3.location.LocationProvider
import kotlinx.coroutines.Dispatchers

class AirlyViewModel(
    val installationRepository: InstallationRepository,
    val measurementsRepository: MeasurementsRepository,
    val locationProvider: LocationProvider
): ViewModel() {

    private val MAX_DISTANCE by lazy { 3.0 } // Kilometers
    private val MAX_RESULTS by lazy { 5 }

    val installations: LiveData<List<Installation>> =
        liveData(Dispatchers.IO) {
            val currentLocation = locationProvider.currentLocation()
            val installationsWrapper = installationRepository.readInstallations(
                currentLocation.first, currentLocation.second, MAX_DISTANCE, MAX_RESULTS
            )
            when {
                installationsWrapper.value == null || installationsWrapper.hasError -> {
                    Log.e(LauncherApplication.LOG_TAG, "Unabled to load installations")
                }
                else -> emit(installationsWrapper.value)
            }
        }

    fun measurements(vararg installationId: Int): LiveData<Measurements> =
        liveData(Dispatchers.IO) {
            measurementsRepository.readMeasurements(*installationId) { measurements ->
                when {
                    measurements.value == null || measurements.hasError -> {
                        Log.e(LauncherApplication.LOG_TAG, "Unabled to load measurements")
                    }
                    else -> emit(measurements.value)
                }
            }
        }
}
