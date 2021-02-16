package com.darekbx.launcher3.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.darekbx.launcher3.BuildConfig
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
) : ViewModel() {

    val installations: LiveData<List<Installation>> =
        liveData(Dispatchers.IO) {
            val currentLocation = locationProvider.currentLocation()
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

    fun measurements(installationIds: List<Int>): LiveData<Measurements> =
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
}
