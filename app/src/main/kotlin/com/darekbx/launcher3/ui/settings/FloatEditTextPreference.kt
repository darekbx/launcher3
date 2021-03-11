package com.darekbx.launcher3.ui.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.EditTextPreference

class FloatEditTextPreference(context: Context, attrs: AttributeSet?) :
    EditTextPreference(context, attrs) {

    override fun getPersistedString(defaultReturnValue: String?): String {
        return "${getPersistedFloat(defaultValue)}"
    }

    override fun persistString(value: String?): Boolean {
        return persistFloat(value?.toFloat() ?: defaultValue)
    }

    private val defaultValue by lazy { -1F }
}
