package com.darekbx.launcher3.viewmodel

import android.location.Location
import androidx.lifecycle.*
import com.darekbx.launcher3.BuildConfig
import com.darekbx.launcher3.airly.data.InstallationRepository
import com.darekbx.launcher3.airly.data.MeasurementsRepository
import com.darekbx.launcher3.airly.domain.Coordinates
import com.darekbx.launcher3.airly.domain.DistanceMeasurements
import com.darekbx.launcher3.airly.domain.Installation
import com.darekbx.launcher3.airly.domain.Measurements
import com.darekbx.launcher3.format
import com.darekbx.launcher3.location.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AirlyViewModel(
    private val installationRepository: InstallationRepository,
    private val measurementsRepository: MeasurementsRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private var currentLocation: Location = Location("Unknown")

    val installations = MutableLiveData<List<Installation>>()
    val measurements = MutableLiveData<Measurements>()
    val distanceMeasurements = Transformations.map(measurements, { measurement ->
        val measurementInstallation =
            installations.value?.find { it.id == measurement.installationId }
        var distanceToLocation = ""
        if (measurementInstallation != null) {
            val distance = distanceToInstallation(measurementInstallation.location)
            distanceToLocation = (distance / 1000).format(1)
        }
        DistanceMeasurements(measurement, distanceToLocation)
    })

    fun loadInstallations() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val location = locationProvider.currentLocation()
                if (location != null) {
                    currentLocation = location
                    val installationsWrapper = installationRepository.readInstallations(
                        currentLocation.latitude, currentLocation.longitude,
                        BuildConfig.AIRLY_MAX_DISTANCE, BuildConfig.AIRLY_MAX_RESULTS
                    )
                    when {
                        installationsWrapper.value == null || installationsWrapper.hasError -> {
                            Timber.e("Unabled to load installations")
                        }
                        else -> installations.postValue(installationsWrapper.value!!)
                    }
                }
            }
        }
    }

    fun loadMeasurements(installationIds: List<Int>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                measurementsRepository.readMeasurements(installationIds) { measurementsWrapper ->
                    when {
                        measurementsWrapper.value == null || measurementsWrapper.hasError -> {
                            Timber.e("Unabled to load measurements")
                        }
                        else -> measurements.postValue(measurementsWrapper.value!!)
                    }
                }
            }
        }
    }

    private fun distanceToInstallation(coordinates: Coordinates): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            coordinates.latitude, coordinates.longitude,
            currentLocation.latitude, currentLocation.longitude,
            results
        )
        return results[0].toDouble()
    }
}
