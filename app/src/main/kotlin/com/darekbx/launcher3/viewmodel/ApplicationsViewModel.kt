package com.darekbx.launcher3.viewmodel

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darekbx.launcher3.ui.applications.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApplicationsViewModel(
    private val packageManager: PackageManager,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val DEFAULT_POSITION = Int.MAX_VALUE
        private const val APP_PREFERENCE_PREFIX = "application_"
    }

    val applications = MutableLiveData<List<Application>>()

    fun listApplications() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val launcherIntent = provideLauncherIntent()
                    val activities = packageManager.queryIntentActivities(launcherIntent, 0)
                    val applicationsList = activities.map { resolveInfo ->
                        val label = loadLabel(resolveInfo)
                        val packageName = loadPackageName(resolveInfo)
                        val icon = loadAppIcon(packageName)
                        val position = readPosition(packageName)
                        Application(label, packageName, icon, position)
                    }
                    applications.postValue(applicationsList.sortedBy { it.position })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun provideLauncherIntent(): Intent {
        return Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
    }

    fun loadAppIcon(packageName: String) = packageManager.getApplicationIcon(packageName)

    fun loadPackageName(resolveInfo: ResolveInfo) =
        resolveInfo.activityInfo.applicationInfo.packageName

    fun loadLabel(resolveInfo: ResolveInfo) =
        resolveInfo.loadLabel(packageManager).toString()

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
