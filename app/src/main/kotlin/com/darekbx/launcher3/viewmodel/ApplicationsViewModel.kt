package com.darekbx.launcher3.viewmodel

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.darekbx.launcher3.ui.applications.Application
import kotlinx.coroutines.Dispatchers

class ApplicationsViewModel(
    private val packageManager: PackageManager,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val DEFAULT_POSITION = Int.MAX_VALUE
        private const val APP_PREFERENCE_PREFIX = "application_"
    }

    fun listApplications() = liveData(Dispatchers.IO) {
        try {
            val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val activities = packageManager.queryIntentActivities(launcherIntent, 0)
            val applications = activities.map { resolveInfo ->
                val label = resolveInfo.loadLabel(packageManager).toString()
                val packageName = resolveInfo.activityInfo.applicationInfo.packageName
                val icon = packageManager.getApplicationIcon(packageName)
                val position = readPosition(packageName)
                Application(label, packageName, icon, position)
            }
            emit(applications.sortedBy { it.position })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun savePosition(application: Application, position: Int) {
        sharedPreferences
            .edit()
            .putInt("$APP_PREFERENCE_PREFIX${application.packageName}", position)
            .apply()
    }

    private fun readPosition(packageName: String): Int {
        return sharedPreferences.getInt(
            "$APP_PREFERENCE_PREFIX${packageName}",
            DEFAULT_POSITION
        )
    }
}
