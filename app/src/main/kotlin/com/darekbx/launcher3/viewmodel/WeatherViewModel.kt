package com.darekbx.launcher3.viewmodel

import android.graphics.Bitmap
import android.util.MalformedJsonException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.launcher3.location.LocationProvider
import com.darekbx.launcher3.weather.AntistormDataSource
import com.darekbx.launcher3.weather.RainviewerDataSource
import com.darekbx.launcher3.weather.WeatherDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalStateException

class WeatherViewModel(
    private val locationProvider: LocationProvider,
    private val rainviewerDataSource: RainviewerDataSource,
    private val antistormDataSource: AntistormDataSource
) : ViewModel() {

    val rainPrediction = MutableLiveData<Bitmap>()
    val error = MutableLiveData<Exception>()

    fun rainPrediction(useAntiStorm: Boolean = false) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val location = locationProvider.currentLocation()
                    if (location != null) {
                        val rainPredictionImage =
                            weatherDataSource(useAntiStorm).downloadRainPrediction(
                                location.latitude, location.longitude
                            )
                        rainPrediction.postValue(rainPredictionImage)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                error.postValue(e)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                error.postValue(e)
            } catch (e: MalformedJsonException) {
                e.printStackTrace()
                error.postValue(e)
            }
        }
    }

    private fun weatherDataSource(useAntiStorm: Boolean): WeatherDataSource {
        return when (useAntiStorm) {
            true -> antistormDataSource
            else -> rainviewerDataSource
        }
    }
}
