package com.tayadehritik.busapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LatLngMarker::class], version = 8)
abstract class AppDatabase:RoomDatabase() {
    abstract fun routeCollectionDAO(): RouteCollectionDAO
}