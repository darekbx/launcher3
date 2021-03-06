package com.darekbx.launcher3.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.darekbx.launcher3.R
import com.darekbx.launcher3.databinding.ActivityMainBinding
import com.darekbx.launcher3.screenon.ScreenOnReceiver
import com.darekbx.launcher3.ui.airly.AirlyFragment
import com.darekbx.launcher3.ui.screenon.ScreenOnFragment
import com.darekbx.launcher3.ui.sunrisesunset.SunriseSunsetFragment
import com.darekbx.launcher3.ui.weather.WeatherFragment

class MainActivity : AppCompatActivity() {

    private val screenOnReceiver by lazy { ScreenOnReceiver() }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleGlobalRefresh()

        permissionRequester.runWithPermissions {
            if (supportFragmentManager.fragments.isEmpty()) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.airly_fragment, AirlyFragment())
                    .add(R.id.weather_fragment, WeatherFragment())
                    .add(R.id.sunrise_sunset_fragment, SunriseSunsetFragment())
                    .add(R.id.screen_on_fragment, ScreenOnFragment())
                    .commit()
            }
        }

        registerReceiver(screenOnReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
    }

    private val permissionRequester =
        PermissionRequester(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            onDenied = { showRationale() },
            onShowRationale = { showDeniedPermissionInformation() }
        )

    private fun showRationale() {
        Toast.makeText(this, R.string.location_permission_rationale, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showDeniedPermissionInformation() {
        Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT)
            .show()
    }
    private fun handleGlobalRefresh() {
        binding.globalRefresh.setOnClickListener {
            supportFragmentManager.fragments
                .filter { it is RefreshableFragment }
                .forEach { (it as RefreshableFragment).refresh() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        unregisterReceiver(screenOnReceiver)
    }
}
