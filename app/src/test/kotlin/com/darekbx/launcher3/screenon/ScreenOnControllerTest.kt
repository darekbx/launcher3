package com.darekbx.launcher3.screenon

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.assertEquals

class ScreenOnControllerTest {

    private var currentSystemTime = 0L
    private var dayOfYear = 0

    private val preference: Preferences = mock {
        on { get(longPreferencesKey("dailyTimeKey")) } doReturn 0L
        on { get(intPreferencesKey("dayOfYearKey")) } doReturn 10
    }
    private val dataStore: DataStore<Preferences> = mock {
        on { data } doReturn flowOf(preference)
    }

    private val screenOnController = spy(ScreenOnController(dataStore, { currentSystemTime }, { dayOfYear }))

    @Test
    fun `User was present once`() = runBlocking {
        // Given
        notifyUser(10, 1000L, 5000L)

        // When
        screenOnController.currentDailyTime().collect { }

        // ThendoReturn
        verify(screenOnController, times(1)).saveDailyTime(eq(4000L))
        verify(screenOnController, times(1)).saveDayOfYear(eq(10))
    }

    @Test
    fun `User was present two times`() = runBlocking {
        // Given
        notifyUser(10, 1000L, 5000L)
        notifyUser(10, 8000L, 9000L)

        // Then
        val dailyTimeCaptor = argumentCaptor<Long>()
        verify(screenOnController, times(2)).saveDailyTime(dailyTimeCaptor.capture())
        assertEquals(4000L, dailyTimeCaptor.firstValue)
        assertEquals(1000L, dailyTimeCaptor.secondValue)

        verify(screenOnController, times(2)).saveDayOfYear(eq(10))
    }

    @Test
    fun `Day was changed and daily time was being reset`() = runBlocking {
        // Given
        notifyUser(10, 1000L, 5000L)

        // When
        notifyUser(11, 7000L, 9000L)

        // Then
        val dailyTimeCaptor = argumentCaptor<Long>()
        verify(screenOnController, times(2)).saveDailyTime(dailyTimeCaptor.capture())
        assertEquals(4000L, dailyTimeCaptor.firstValue)
        assertEquals(2000L, dailyTimeCaptor.secondValue)

        val dayOfYearCaptor = argumentCaptor<Int>()
        verify(screenOnController, times(2)).saveDayOfYear(dayOfYearCaptor.capture())
        assertEquals(10, dayOfYearCaptor.firstValue)
        assertEquals(11, dayOfYearCaptor.secondValue)
    }

    private suspend fun notifyUser(day: Int, userPresentTime: Long, screenOfTime: Long) {
        dayOfYear = day
        currentSystemTime = userPresentTime
        screenOnController.notifyUserPresent()
        currentSystemTime = screenOfTime
        screenOnController.notifyScreenOff()
    }
}
