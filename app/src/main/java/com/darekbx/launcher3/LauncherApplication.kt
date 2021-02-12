package com.darekbx.launcher3

import android.app.Application
import com.darekbx.launcher3.airly.data.*
import com.darekbx.launcher3.location.LocationProvider
import com.darekbx.launcher3.viewmodel.AirlyViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class LauncherApplication : Application() {

    companion object {
        const val LOG_TAG = "Launcher3"
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
