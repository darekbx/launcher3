package com.darekbx.launcher3

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.darekbx.launcher3.airly.data.*
import com.darekbx.launcher3.location.LocationProvider
import com.darekbx.launcher3.screenon.ScreenOnController
import com.darekbx.launcher3.viewmodel.AirlyViewModel
import com.darekbx.launcher3.viewmodel.AntistormViewModel
import com.darekbx.launcher3.viewmodel.ScreenOnViewModel
import com.darekbx.launcher3.viewmodel.SunriseSunsetViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber
import timber.log.Timber.DebugTree

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
        viewModel { SunriseSunsetViewModel(get()) }
        viewModel { ScreenOnViewModel(get()) }
        viewModel { AntistormViewModel(get()) }
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

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}
