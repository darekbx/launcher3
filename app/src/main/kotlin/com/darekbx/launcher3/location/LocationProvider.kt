package com.darekbx.launcher3.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationProvider(
    private val fusedLocationProviderClient: FusedLocationProviderClient
){
    @SuppressLint("MissingPermission")
    suspend fun currentLocation(): Location? = suspendCoroutine { continuation ->
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                continuation.resume(location)
            }
        }
    }
}
