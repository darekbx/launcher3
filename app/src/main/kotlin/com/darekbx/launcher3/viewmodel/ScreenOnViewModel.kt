package com.darekbx.launcher3.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.darekbx.launcher3.screenon.ScreenOnController
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit

class ScreenOnViewModel(
    private val screenOnController: ScreenOnController
) : ViewModel() {

    val screenOn: LiveData<Long> = liveData(Dispatchers.IO) {
        val dailyTime = screenOnController.currentDailyTime() / TimeUnit.SECONDS.toMillis(1)
        emit(dailyTime)
    }
}
