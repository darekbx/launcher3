package com.darekbx.launcher3.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.darekbx.launcher3.BuildConfig
import com.darekbx.launcher3.R
import com.darekbx.launcher3.weather.RainviewerDataSource

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    var onPreferenceChangedListener: (() -> Unit) = { }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setDefaultValues()
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    private fun setDefaultValues() {
        val preferences = preferenceManager.sharedPreferences
        with(preferences.edit()) {
            if (!preferences.contains("airly_max_results")) {
                putInt("airly_max_results", BuildConfig.AIRLY_MAX_RESULTS)
            }
            if (!preferences.contains("airly_max_distance")) {
                putFloat("airly_max_distance", BuildConfig.AIRLY_MAX_DISTANCE.toFloat())
            }
            if (!preferences.contains("rainviewer_zoom")) {
                putInt("rainviewer_zoom", RainviewerDataSource.DEFAULT_ZOOM)
            }
            apply()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        onPreferenceChangedListener()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
