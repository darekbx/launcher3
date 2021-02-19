package com.darekbx.launcher3.ui

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("airlyColor")
fun airlyColor(view: TextView, color: String) {
    view.setTextColor(Color.parseColor(color))
}
