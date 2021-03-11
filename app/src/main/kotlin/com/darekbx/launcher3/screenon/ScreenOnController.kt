package com.darekbx.launcher3.screenon

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.Calendar

open class ScreenOnController(
    private val dataStore: DataStore<Preferences>,
    private val currentTime: () -> Long = { System.currentTimeMillis() },
    private val currentDayOfYear: () -> Int = { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) },
) {

    init {
        Timber.tag("ScreenOn")
    }

    class ScreenOnPreferences(val dailyTime: Long, val dayOfYear: Int)

    private val magicZero = 0L
    private val dailyTimeKey = longPreferencesKey("dailyTimeKey")
    private val dayOfYearKey = intPreferencesKey("dayOfYearKey")
    private var startTimestamp: Long = 0L

    fun currentDailyTime() = screenOnPreferences

    suspend fun notifyScreenOff() {
        Timber.d("notifyScreenOff")
        if (startTimestamp == 0L) {
            // Session initialize when screen was turned on
            return
        }
        val timeSpent = currentTime() - startTimestamp
        val preferences = screenOnPreferences.first()
        val dailyTime = when {
            preferences.dayOfYear == currentDayOfYear() -> preferences.dailyTime
            else -> 0L
        }
        saveDailyTime(dailyTime + timeSpent)
        saveDayOfYear(currentDayOfYear())
    }

    fun notifyUserPresent() {
        Timber.d("notifyUserPresent")
        startTimestamp = currentTime()
    }

    val screenOnPreferences: Flow<ScreenOnPreferences> = dataStore.data
        .map { preferences ->
            ScreenOnPreferences(
                preferences[dailyTimeKey] ?: magicZero,
                preferences[dayOfYearKey] ?: magicZero.toInt()
            )
        }

    open suspend fun saveDailyTime(value: Long) {
        dataStore.edit { settings ->
            settings[dailyTimeKey] = value
        }
    }

    open suspend fun saveDayOfYear(value: Int) {
        dataStore.edit { settings ->
            settings[dayOfYearKey] = value
        }
    }
}
