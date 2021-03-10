package com.darekbx.launcher3.dotpad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DotsReceiver : BroadcastReceiver() {

    companion object {
        const val DOTS_COUNT_KEY = "dotsCount"
        const val DOTS_FORWARD_ACTION = "forwardDotsCount"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        if (intent.hasExtra(DOTS_COUNT_KEY) && extras != null) {
            context.sendBroadcast(Intent().apply {
                action = DOTS_FORWARD_ACTION
                putExtras(extras)
            })
        }
    }
}
