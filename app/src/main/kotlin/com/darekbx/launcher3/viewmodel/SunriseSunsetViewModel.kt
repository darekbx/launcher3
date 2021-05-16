package com.darekbx.launcher3.viewmodel

import androidx.lifecycle.*
import com.darekbx.launcher3.location.LocationProvider
import com.darekbx.launcher3.utils.SunriseSunsetWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SunriseSunsetViewModel(
    private val locationProvider: LocationProvider,
    private val sunriseSunsetWrapper: SunriseSunsetWrapper,
    private val calendar: Calendar = Calendar.getInstance()
) : ViewModel() {

    class SunriseSunsetData(val sunrise: String, val sunset: String)

    fun sunriseSunset(): LiveData<SunriseSunsetData> {
        val sunriseSunsetData = MutableLiveData<SunriseSunsetData>()
        viewModelScope.launch {
            val calendars = loadSunriseSunset()
            if (calendars == null) {
                sunriseSunsetData.postValue(SunriseSunsetData("--:--", "--:--"))
                return@launch
            }
            val sunriseSunset = SunriseSunsetData(
                sunrise = calendars[0].toFormattedTime(),
                sunset = calendars[1].toFormattedTime()
            )
            sunriseSunsetData.postValue(sunriseSunset)
        }
        return sunriseSunsetData
    }

    private suspend fun loadSunriseSunset(): Array<out Calendar>? {
        val currentLocation = locationProvider.currentLocation()
        if (currentLocation != null) {
            return sunriseSunsetWrapper.getSunriseSunset(
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
