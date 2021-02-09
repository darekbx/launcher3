package com.darekbx.launcher3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darekbx.airly.data.AirlyDataSource
import com.darekbx.airly.data.InstallationRepository
import com.darekbx.airly.interactions.ReadInstallations
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val source = AirlyDataSource()
                val repo = InstallationRepository(source)
                ReadInstallations(repo).invoke(51.0, 21.0, 3.0, 5)
            }
        }
    }
}
