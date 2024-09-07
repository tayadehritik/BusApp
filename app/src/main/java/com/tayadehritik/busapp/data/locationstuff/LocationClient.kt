package com.tayadehritik.busapp.data.locationstuff

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tayadehritik.busapp.data.local.AppDatabase
import com.tayadehritik.busapp.hasLocationPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationClient(
    private val appContext: Application,
    private val appDatabase: AppDatabase
):MyLocationManager {


    private val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)
    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            if(!appContext.hasLocationPermission()) {
                throw MyLocationManager.LocationException("Missing Location Permission")
            }

            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(!isGPSEnabled && !isNetworkEnabled) {
                throw MyLocationManager.LocationException("GPS is disabled")
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,interval).build()
            val locationCallback = object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.locations.lastOrNull()?.let { location ->
                        launch { trySend(location) }
                    }
                }
            }
            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                appDatabase.routeCollectionDAO().clearRoute()
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}