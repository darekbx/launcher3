package com.darekbx.launcher3.airly.domain

data class RateLimits(
    val dayLimit: Int,
    val dayRemaining: Int,
    val minuteLimit: Int,
    val minuteRemaining: Int
) {

    val isEmpty: Boolean
        get() = dayLimit == -1 && dayRemaining == -1 && minuteLimit == -1 && minuteRemaining == -1
}
