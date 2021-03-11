package com.darekbx.launcher3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import ca.rmen.sunrisesunset.SunriseSunset
import com.darekbx.launcher3.location.LocationProvider
import kotlinx.coroutines.Dispatchers
import java.util.Calendar

class SunriseSunsetViewModel(
    private val locationProvider: LocationProvider,
    private val calendar: Calendar = Calendar.getInstance()
) : ViewModel() {

    class SunriseSunsetData(val sunrise: String, val sunset: String)

    val sunriseSunset: LiveData<SunriseSunsetData> = liveData(Dispatchers.IO) {
        val calendars = loadSunriseSunset() ?: return@liveData
        val sunriseSunset = SunriseSunsetData(
            sunrise = calendars[0].toFormattedTime(),
            sunset = calendars[1].toFormattedTime()
        )
        emit(sunriseSunset)
    }

    private suspend fun loadSunriseSunset(): Array<out Calendar>? {
        val currentLocation = locationProvider.currentLocation()
        if (currentLocation != null) {
            return SunriseSunset.getSunriseSunset(
                calendar,
                currentLocation.latitude, currentLocation.longitude
            )
        }
        return null
    }

    private fun Calendar.toFormattedTime(): String {
        val hour = get(Calendar.HOUR_OF_DAY).toString()
        val minute = get(Calendar.MINUTE).toString()
        return "${hour.padStart(2, '0')}:${minute.padStart(2, '0')}"
    }
}
