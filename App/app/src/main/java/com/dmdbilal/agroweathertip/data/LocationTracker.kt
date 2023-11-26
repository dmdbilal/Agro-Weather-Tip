package com.dmdbilal.agroweathertip.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.DisposableEffect
import androidx.core.app.ActivityCompat
import com.dmdbilal.agroweathertip.presentation.CropViewModel
import com.google.android.gms.location.LocationRequest
import java.util.concurrent.TimeUnit

class LocationTracker(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val viewModel: CropViewModel
) {
    private val LOCATION_PERMISSION_REQ_CODE = 1000
    private val TAG = "LocationTracker"

     fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE)
            return
        }

        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                viewModel.setLocation(listOf(location.latitude, location.longitude))
//                Toast.makeText(context, "Lat: ${location.latitude} Long: ${location.longitude}", Toast.LENGTH_SHORT).show()
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if(location == null) {
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    return@addOnSuccessListener
                } else {
                    location.apply {
                        viewModel.setLocation(listOf(latitude, longitude))
//                        Toast.makeText(context, "Lat: $latitude Long: $longitude", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed on getting current location", Toast.LENGTH_SHORT).show()
            }

    }
}