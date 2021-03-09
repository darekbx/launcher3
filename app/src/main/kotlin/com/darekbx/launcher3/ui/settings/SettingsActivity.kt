package com.darekbx.launcher3.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darekbx.launcher3.R

class SettingsActivity: AppCompatActivity(R.layout.activity_settings) {

    companion object {
        val PREFERENCES_WERE_CHANGED = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_fragment, SettingsFragment().apply {
                onPreferenceChangedListener = { setResult(PREFERENCES_WERE_CHANGED) }
            })
            .commit()
    }
}
