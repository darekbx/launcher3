package com.darekbx.launcher3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.darekbx.launcher3.screenon.ScreenOnController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect

class ScreenOnViewModel(
    private val screenOnController: ScreenOnController
) : ViewModel() {

    val screenOn: LiveData<Long> = liveData(Dispatchers.IO) {
        screenOnController.currentDailyTime().collect { screenOnPreferences ->
            val seconds = screenOnPreferences.dailyTime / 1000L
            emit(seconds)
        }
    }
}
