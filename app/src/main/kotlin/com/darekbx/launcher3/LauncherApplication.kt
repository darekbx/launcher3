package com.darekbx.launcher3

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.darekbx.launcher3.airly.data.AirlyDataSource
import com.darekbx.launcher3.airly.data.InstallationDataSource
import com.darekbx.launcher3.airly.data.InstallationRepository
import com.darekbx.launcher3.airly.data.MeasurementsDataSource
import com.darekbx.launcher3.airly.data.MeasurementsRepository
import com.darekbx.launcher3.location.LocationProvider
import com.darekbx.launcher3.screenon.ScreenOnController
import com.darekbx.launcher3.utils.HttpTools
import com.darekbx.launcher3.viewmodel.AirlyViewModel
import com.darekbx.launcher3.viewmodel.WeatherViewModel
import com.darekbx.launcher3.viewmodel.ScreenOnViewModel
import com.darekbx.launcher3.viewmodel.SunriseSunsetViewModel
import com.darekbx.launcher3.weather.AntistormDataSource
import com.darekbx.launcher3.weather.PositionMarker
import com.darekbx.launcher3.weather.RainviewerDataSource
import com.google.android.gms.location.FusedLocationProviderClient
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
        const val PREFERENCES_NAME = "Launcher3Preferences"
    }

    val commonModule = module {
        single { HttpTools() }
        single { AirlyDataSource.AirlyHttpTools(BuildConfig.AIRLY_API_KEY) }
        single { LocationProvider(get()) }
        single { (get() as Context).dataStore }
        single { ScreenOnController(get()) }
        single { FusedLocationProviderClient(get() as Context) }
    }

    val weatherModule = module {
        single { PositionMarker() }
        single { RainviewerDataSource(get(), get()) }
        single { AntistormDataSource(get(), get()) }
    }

    val airlyModule = module {
        single { AirlyDataSource(get()) }
        single<InstallationDataSource> { get() as AirlyDataSource }
        single<MeasurementsDataSource> { get() as AirlyDataSource }
        single { InstallationRepository(get()) }
        single { MeasurementsRepository(get()) }
    }

    val viewModelModule = module {
        viewModel { AirlyViewModel(get(), get(), get()) }
        viewModel { SunriseSunsetViewModel(get()) }
        viewModel { ScreenOnViewModel(get()) }
        viewModel { WeatherViewModel(get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }
            androidContext(this@LauncherApplication)
            modules(commonModule, weatherModule, airlyModule, viewModelModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}
