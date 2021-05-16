package com.darekbx.launcher3.utils

import ca.rmen.sunrisesunset.SunriseSunset
import java.util.*

class SunriseSunsetWrapper {

    fun getSunriseSunset(calendar: Calendar, latitude: Double, longitude: Double): Array<Calendar> {
        return SunriseSunset.getSunriseSunset(calendar, latitude, longitude)
    }
}
