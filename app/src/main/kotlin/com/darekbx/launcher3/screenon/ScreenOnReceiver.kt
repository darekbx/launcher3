package com.darekbx.launcher3.screenon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScreenOnReceiver : BroadcastReceiver(), KoinComponent {

    private val screenOnController: ScreenOnController by inject()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_USER_PRESENT -> screenOnController.notifyUserPresent()
            Intent.ACTION_SCREEN_OFF -> {
                CoroutineScope(Dispatchers.IO).launch {
                    screenOnController.notifyScreenOff()
                }
            }
        }
    }
}
