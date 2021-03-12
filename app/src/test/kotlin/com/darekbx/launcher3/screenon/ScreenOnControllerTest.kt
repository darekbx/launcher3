package com.darekbx.launcher3.screenon

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.*
import org.junit.After
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ScreenOnControllerTest {

    private var currentSystemTime = 0L
    private var dayOfYear = 0

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `User was present once`() {
        // Given
        notifyUser(10, 1000L, 5000L)

        // When
        val dailyTime = screenOnController.currentDailyTime()

        // Then
        assertEquals(4000L, dailyTime)
    }

    @Test
    fun `User was present two times`() {
        // Given
        notifyUser(10, 1000L, 5000L)
        notifyUser(10, 8000L, 9000L)

        // When
        val dailyTime = screenOnController.currentDailyTime()

        // Then
        assertEquals(5000L, dailyTime)
    }

    @Test
    fun `Day was changed and daily time was being reset`() {
        // Given
        notifyUser(10, 1000L, 5000L)

        // When
        notifyUser(11, 7000L, 9000L)

        // Then
        val dailyTime = screenOnController.currentDailyTime()

        // Then
        assertEquals(2000L, dailyTime)
    }

    private fun notifyUser(day: Int, userPresentTime: Long, screenOfTime: Long) {
        dayOfYear = day
        currentSystemTime = userPresentTime
        screenOnController.notifyUserPresent()
        currentSystemTime = screenOfTime
        screenOnController.notifyScreenOff()
    }

    private val inMemorySharedPreferences = object : SharedPreferences {
        private val data = mutableMapOf<String, Any>()

        override fun getAll(): MutableMap<String, *> = data

        override fun getString(key: String?, defValue: String?): String? = data[key]?.toString()

        override fun getStringSet(
            key: String?,
            defValues: MutableSet<String>?
        ): MutableSet<String> = emptySet<String>().toMutableSet()

        override fun getInt(key: String?, defValue: Int): Int =
            data[key]?.toString()?.toInt() ?: defValue

        override fun getLong(key: String?, defValue: Long): Long =
            data[key]?.toString()?.toLong() ?: defValue

        override fun getFloat(key: String?, defValue: Float): Float =
            data[key]?.toString()?.toFloat() ?: defValue

        override fun getBoolean(key: String?, defValue: Boolean): Boolean =
            data[key]?.toString()?.toBoolean() ?: defValue

        override fun contains(key: String?): Boolean = data.containsKey(key)

        override fun edit(): SharedPreferences.Editor = object : SharedPreferences.Editor {

            override fun putString(key: String?, value: String?): SharedPreferences.Editor {
                data[key ?: ""] = value ?: ""
                return this
            }

            override fun putStringSet(
                key: String?,
                values: MutableSet<String>?
            ): SharedPreferences.Editor {
                return this
            }

            override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
                data[key ?: ""] = value
                return this
            }

            override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
                data[key ?: ""] = value
                return this
            }

            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
                data[key ?: ""] = value
                return this
            }

            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
                data[key ?: ""] = value
                return this
            }

            override fun remove(key: String?): SharedPreferences.Editor {
                return this
            }

            override fun clear(): SharedPreferences.Editor {
                return this
            }

            override fun commit(): Boolean {
                return true
            }

            override fun apply() {}
        }

        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
    }

    private val screenOnController =
        spy(ScreenOnController(inMemorySharedPreferences, { currentSystemTime }, { dayOfYear }))
}
