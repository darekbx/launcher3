package com.darekbx.launcher3.utils

import android.content.res.Resources

class ScreenUtils {

    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels
}
