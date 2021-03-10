package com.darekbx.launcher3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.darekbx.launcher3.screenon.ScreenOnController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit

class ScreenOnViewModel(
    private val screenOnController: ScreenOnController
) : ViewModel() {

    val screenOn: LiveData<Long> = liveData(Dispatchers.IO) {
        screenOnController.currentDailyTime().collect { screenOnPreferences ->
            val seconds = screenOnPreferences.dailyTime / TimeUnit.SECONDS.toMillis(1)
            emit(seconds)
        }
    }
}
