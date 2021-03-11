package com.darekbx.launcher3.viewmodel

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.darekbx.launcher3.TestCoroutineRule
import com.darekbx.launcher3.location.LocationProvider
import com.nhaarman.mockitokotlin2.*
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SunriseSunsetViewModelTest : TestCase() {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var locationProvider: LocationProvider

    @Mock
    private lateinit var observer: Observer<SunriseSunsetViewModel.SunriseSunsetData>

    @Test
    fun `Sunrise and sunset have correct values`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val location = mock<Location> {
                on { latitude } doReturn 21.0
                on { longitude } doReturn 50.0
            }

            doReturn(location).whenever(locationProvider).currentLocation()

            val calendar = Calendar.Builder().setDate(2020, 5, 25).build()
            val sunsetViewModel = SunriseSunsetViewModel(locationProvider, calendar)

            // When
            sunsetViewModel.sunriseSunset().observeForever(observer)

            // Then
            val captor = argumentCaptor<SunriseSunsetViewModel.SunriseSunsetData>()
            verify(observer, times(1)).onChanged(captor.capture())

            with(captor.firstValue) {
                assertEquals("04:01", sunrise)
                assertEquals("17:26", sunset)
            }
        }
    }
}
