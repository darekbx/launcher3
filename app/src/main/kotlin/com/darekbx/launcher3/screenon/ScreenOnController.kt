package com.darekbx.launcher3.screenon

import android.content.SharedPreferences
import timber.log.Timber
import java.util.Calendar

open class ScreenOnController(
    private val sharedPreferences: SharedPreferences,
    private val currentTime: () -> Long = { System.currentTimeMillis() },
    private val currentDayOfYear: () -> Int = { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) },
) {

    init {
        Timber.tag("ScreenOn")
    }

    private val magicZero = 0L
    private val dailyTimeKey = "dailyTimeKey"
    private val dayOfYearKey = "dayOfYearKey"
    private var startTimestamp: Long = 0L

    fun currentDailyTime() = dailyTime()

    fun notifyScreenOff() {
        Timber.d("notifyScreenOff")
        if (startTimestamp == 0L) {
            // Session initialize when screen was turned on
            return
        }
        val timeSpent = currentTime() - startTimestamp
        saveDailyTime(dailyTime() + timeSpent)
        saveDayOfYear(currentDayOfYear())
    }

    fun notifyUserPresent() {
        Timber.d("notifyUserPresent")
        startTimestamp = currentTime()

        val currentDayOfYear = currentDayOfYear()
        if (dayOfYear() != currentDayOfYear) {
            saveDailyTime(0L)
            saveDayOfYear(currentDayOfYear)
        }
    }

    private fun dailyTime() = sharedPreferences.getLong(dailyTimeKey, magicZero)

    private fun dayOfYear() = sharedPreferences.getInt(dayOfYearKey, magicZero.toInt())

    private fun saveDailyTime(value: Long) {
        sharedPreferences.edit().putLong(dailyTimeKey, value).apply()
    }

    private fun saveDayOfYear(value: Int) {
        sharedPreferences.edit().putInt(dayOfYearKey, value).apply()
    }
}
