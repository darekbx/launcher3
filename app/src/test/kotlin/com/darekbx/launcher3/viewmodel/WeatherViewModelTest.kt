package com.darekbx.launcher3.viewmodel

import android.graphics.Bitmap
import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.darekbx.launcher3.TestCoroutineRule
import com.darekbx.launcher3.location.LocationProvider
import com.darekbx.launcher3.weather.AntistormDataSource
import com.darekbx.launcher3.weather.RainviewerDataSource
import com.nhaarman.mockitokotlin2.*
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest : TestCase() {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var locationProvider: LocationProvider

    @Mock
    private lateinit var rainviewerDataSource: RainviewerDataSource

    @Mock
    private lateinit var antistormDataSource: AntistormDataSource

    @Mock
    private lateinit var observer: Observer<Bitmap>

    @After
    fun cleanUp() {
        reset(rainviewerDataSource, antistormDataSource)
    }

    @Test
    fun `Rainviewer tile was downloaded`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val tile = mock<Bitmap>()
            val location = mock<Location> {
                on { latitude } doReturn 21.0
                on { longitude } doReturn 50.0
            }

            doReturn(location).whenever(locationProvider).currentLocation()
            doReturn(tile).whenever(rainviewerDataSource).downloadRainPrediction(any(), any())
            doReturn(tile).whenever(antistormDataSource).downloadRainPrediction(any(), any())
            val sunsetViewModel =
                WeatherViewModel(locationProvider, rainviewerDataSource, antistormDataSource)

            // When
            sunsetViewModel.rainPrediction.observeForever(observer)
            sunsetViewModel.rainPrediction(false)

            // Then
            verify(observer, times(1)).onChanged(isA())
            verify(rainviewerDataSource, times(1)).downloadRainPrediction(eq(21.0), eq(50.0))
            verify(antistormDataSource, never()).downloadRainPrediction(eq(21.0), eq(50.0))
        }
    }

    @Test
    fun `Antistorm tile was downloaded`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val tile = mock<Bitmap>()
            val location = mock<Location> {
                on { latitude } doReturn 21.0
                on { longitude } doReturn 50.0
            }

            doReturn(location).whenever(locationProvider).currentLocation()
            doReturn(tile).whenever(rainviewerDataSource).downloadRainPrediction(any(), any())
            doReturn(tile).whenever(antistormDataSource).downloadRainPrediction(any(), any())
            val sunsetViewModel =
                WeatherViewModel(locationProvider, rainviewerDataSource, antistormDataSource)

            // When
            sunsetViewModel.rainPrediction.observeForever(observer)
            sunsetViewModel.rainPrediction(useAntiStorm = true)

            // Then
            verify(observer, times(1)).onChanged(isA())
            verify(antistormDataSource, times(1)).downloadRainPrediction(eq(21.0), eq(50.0))
            verify(rainviewerDataSource, never()).downloadRainPrediction(eq(21.0), eq(50.0))
        }
    }
}
