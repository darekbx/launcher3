package com.darekbx.launcher3.viewmodel

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.darekbx.launcher3.TestCoroutineRule
import com.darekbx.launcher3.ui.applications.Application
import com.nhaarman.mockitokotlin2.*
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ApplicationsViewModelTest : TestCase() {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var packageManager: PackageManager

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var observer: Observer<List<Application>>

    @Test
    fun `One application is listed`() {
        testCoroutineRule.runBlockingTest {
            // Given
            val applicationsViewModel =
                spy(ApplicationsViewModel(packageManager, sharedPreferences))
            doReturn(mock<Intent>()).whenever(applicationsViewModel).provideLauncherIntent()
            doReturn("Label").whenever(applicationsViewModel).loadLabel(any())
            doReturn("co.test").whenever(applicationsViewModel).loadPackageName(any())
            doReturn(mock<Drawable>()).whenever(applicationsViewModel).loadAppIcon(any())
            doReturn(listOf(mock<ResolveInfo>())).whenever(packageManager)
                .queryIntentActivities(any(), any())

            // When
            applicationsViewModel.applications.observeForever(observer)
            applicationsViewModel.listApplications()

            // Then
            val captor = argumentCaptor<List<Application>>()
            verify(observer, times(1)).onChanged(captor.capture())

            with(captor.firstValue) {
                assertEquals(1, size)
                assertEquals("Label", get(0).label)
                assertEquals("co.test", get(0).packageName)
            }

            verify(sharedPreferences, times(1)).getInt(eq("application_co.test"), any())
        }
    }

    @Test
    fun `Position was saved`() {
        // Given
        val editor = mock<SharedPreferences.Editor>()
        doReturn(editor).whenever(sharedPreferences).edit()
        doReturn(editor).whenever(editor).putInt(any(), any())
        val applicationsViewModel = ApplicationsViewModel(packageManager, sharedPreferences)
        val application = Application("Label", "co.test", mock(), 0)

        // When
        applicationsViewModel.savePosition(application, 10)

        // Then
        verify(editor, times(1)).putInt(eq("application_co.test"), eq(10))
        verify(editor, times(1)).apply()
    }
}
