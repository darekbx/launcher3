package com.darekbx.launcher3.ui

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import ca.rmen.sunrisesunset.SunriseSunset
import com.darekbx.launcher3.R
import com.darekbx.launcher3.screenon.ScreenOnReceiver
import java.util.*

class MainActivity : AppCompatActivity() {

    private val screenOnReceiver by lazy { ScreenOnReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        setContentView(R.layout.activity_main)

        registerReceiver(screenOnReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_OFF)
        })

        SunriseSunset.getSunriseSunset(Calendar.getInstance(), 21.0, 52.0)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(screenOnReceiver)
    }
}
