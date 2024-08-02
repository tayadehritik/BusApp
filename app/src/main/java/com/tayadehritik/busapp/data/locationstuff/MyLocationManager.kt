package com.tayadehritik.busapp.data.locationstuff

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface MyLocationManager {
    fun getLocationUpdates(interval:Long):Flow<Location>

    class LocationException(message:String): Exception()
}