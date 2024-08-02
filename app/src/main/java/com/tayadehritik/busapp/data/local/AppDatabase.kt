package com.tayadehritik.busapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LatLngMarker::class], version = 2)
abstract class AppDatabase:RoomDatabase() {
    abstract fun LatLngMarkerDAO(): LatLngMarkerDAO
}