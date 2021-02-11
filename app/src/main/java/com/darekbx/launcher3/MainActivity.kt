package com.darekbx.launcher3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darekbx.launcher3.airly.data.AirlyDataSource
import com.darekbx.launcher3.airly.data.InstallationRepository
import com.darekbx.launcher3.airly.data.MeasurementsRepository
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val source = AirlyDataSource(BuildConfig.AIRLY_API_KEY)
                val installationRepository = InstallationRepository(source)
                val measurementsRepository = MeasurementsRepository(source)
                val installations = installationRepository.readInstallations(52.23579007836946, 20.88571355229092, 3.0, 5)
                val measurements = measurementsRepository.readMeasurements(installations.value!!.get(0).id)

                val c = measurements.get(0).value?.current?.values?.get(0)?.name

            }
        }
    }
}
