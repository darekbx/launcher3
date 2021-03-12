package com.darekbx.launcher3.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.darekbx.launcher3.TestCoroutineRule
import com.darekbx.launcher3.screenon.ScreenOnController
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ScreenOnViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var observer: Observer<Long>

    @Test
    fun testGetScreenOn() {
        testCoroutineRule.runBlockingTest {
            // Given
            val screenOnController = mock<ScreenOnController> {
                on { currentDailyTime() } doReturn 1000_000L
            }
            val screenOnViewModel = ScreenOnViewModel(screenOnController)

            // When
            screenOnViewModel.screenOn.observeForever(observer)

            // Then
            verify(observer, times(1)).onChanged(1000L)
        }
    }
}
