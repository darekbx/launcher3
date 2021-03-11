package com.darekbx.launcher3.viewmodel

import androidx.lifecycle.*
import ca.rmen.sunrisesunset.SunriseSunset
import com.darekbx.launcher3.location.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SunriseSunsetViewModel(
    private val locationProvider: LocationProvider,
    private val calendar: Calendar = Calendar.getInstance()
) : ViewModel() {

    class SunriseSunsetData(val sunrise: String, val sunset: String)

    fun sunriseSunset(): LiveData<SunriseSunsetData> {
        val sunriseSunsetData = MutableLiveData<SunriseSunsetData>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val calendars = loadSunriseSunset() ?: return@withContext
                val sunriseSunset = SunriseSunsetData(
                    sunrise = calendars[0].toFormattedTime(),
                    sunset = calendars[1].toFormattedTime()
                )
                sunriseSunsetData.postValue(sunriseSunset)
            }
        }
        return sunriseSunsetData
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
