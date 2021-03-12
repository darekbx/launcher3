package com.darekbx.launcher3.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.darekbx.launcher3.screenon.ScreenOnController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ScreenOnViewModel(
    private val screenOnController: ScreenOnController
) : ViewModel() {

    val screenOn = MutableLiveData<Long>()

    fun loadScreenOn() {
        CoroutineScope(Dispatchers.IO).launch {
            val dailyTime = screenOnController.currentDailyTime() / TimeUnit.SECONDS.toMillis(1)
            screenOn.postValue(dailyTime)
        }
    }
}
