package com.darekbx.launcher3.screenon

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*

open class ScreenOnController(
    private val dataStore: DataStore<Preferences>,
    private val currentTime: () -> Long = { System.currentTimeMillis() },
    private val currentDayOfYear: () -> Int = { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) },
) {

    init {
        Timber.tag("ScreenOn")
    }

    class ScreenOnPreferences(val dailyTime: Long, val dayOfYear: Int)

    private val MAGIC_ZERO by lazy { 0L }
    private val DAILY_TIME_KEY by lazy { longPreferencesKey("dailyTimeKey") }
    private val DAY_OF_YEAR_KEY by lazy { intPreferencesKey("dayOfYearKey") }
    private var startTimestamp: Long = 0L

    fun currentDailyTime() = screenOnPreferences

    suspend fun notifyScreenOff() {
        Timber.d("notifyScreenOff")
        if (startTimestamp == 0L) {
            // Session initializec when screen was turned on
            return
        }
        val timeSpent = currentTime() - startTimestamp
        val preferences = screenOnPreferences.first()
        var dailyTime = when {
            preferences.dayOfYear == currentDayOfYear() ->  preferences.dailyTime
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
                preferences[DAILY_TIME_KEY] ?: MAGIC_ZERO,
                preferences[DAY_OF_YEAR_KEY] ?: MAGIC_ZERO.toInt()
            )
        }

    open suspend fun saveDailyTime(value: Long) {
        dataStore.edit { settings ->
            settings[DAILY_TIME_KEY] = value
        }
    }

    open suspend fun saveDayOfYear(value: Int) {
        dataStore.edit { settings ->
            settings[DAY_OF_YEAR_KEY] = value
        }
    }
}
