package com.darekbx.launcher3

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.darekbx.launcher3.airly.data.AirlyDataSource
import com.darekbx.launcher3.airly.data.MeasurementsRepository
import com.darekbx.launcher3.airly.data.MeasurementsDataSource
import com.darekbx.launcher3.airly.data.InstallationDataSource
import com.darekbx.launcher3.airly.data.InstallationRepository
import com.darekbx.launcher3.location.LocationProvider
import com.darekbx.launcher3.screenon.ScreenOnController
import com.darekbx.launcher3.viewmodel.AirlyViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class LauncherApplication : Application() {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

    companion object {
        const val LOG_TAG = "Launcher3"
        const val PREFERENCES_NAME = "Launcher3Preferences"
    }

    val commonModule = module {
        single { LocationProvider() }
    }

    val airlyModule = module {
        single { AirlyDataSource(BuildConfig.AIRLY_API_KEY) }
        single<InstallationDataSource> { get() as AirlyDataSource }
        single<MeasurementsDataSource> { get() as AirlyDataSource }
        single { InstallationRepository(get()) }
        single { MeasurementsRepository(get()) }
        single { (get() as Context).dataStore }
        single { ScreenOnController(get()) }
    }

    val viewModelModule = module {
        viewModel { AirlyViewModel(get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }
            androidContext(this@LauncherApplication)
            modules(commonModule, airlyModule, viewModelModule)
        }
    }
}
