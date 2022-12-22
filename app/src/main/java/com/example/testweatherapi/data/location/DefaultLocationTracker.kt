package com.example.testweatherapi.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.testweatherapi.domain.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
): LocationTracker {
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        if (!locationAvailability()) {
            return null
        }
        val currentLocationRequest = LocationRequest()
        currentLocationRequest.setInterval(1)
            .setFastestInterval(0)
            .setMaxWaitTime(0)
            .setNumUpdates(1)
            .setSmallestDisplacement(0f).priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
            }
        }
        return suspendCancellableCoroutine { cont ->
            locationClient.requestLocationUpdates(
                currentLocationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            locationClient.lastLocation.apply {
                if(isComplete) {
                    if(isSuccessful) {
                        Log.v("LocationTracker", "Location - $result")
                        cont.resume(result)
                    } else {
                        cont.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    cont.resume(it)
                }
                addOnFailureListener {
                    cont.resume(null)
                }
                addOnCanceledListener {
                    cont.cancel()
                }
            }
        }
    }

    private fun locationAvailability(): Boolean {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return hasAccessFineLocationPermission || hasAccessCoarseLocationPermission || isGpsEnabled
    }
}