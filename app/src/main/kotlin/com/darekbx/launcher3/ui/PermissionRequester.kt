package com.darekbx.launcher3.ui

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionRequester(
    activity: ComponentActivity,
    private val permission: String,
    onDenied: () -> Unit = { },
    onShowRationale: () -> Unit = { }
) {
    private var onGranted: () -> Unit = { }

    private val launcher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> onGranted()
            activity.shouldShowRequestPermissionRationale(permission) -> onShowRationale()
            else -> onDenied()
        }
    }

    fun runWithPermissions(onGranted: () -> Unit) {
        this.onGranted = onGranted
        launcher.launch(permission)
    }
}
