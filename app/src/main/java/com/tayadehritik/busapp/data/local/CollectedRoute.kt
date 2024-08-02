package com.tayadehritik.busapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "CollectedRoute")
data class LatLngMarker(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name="lng") val lng: Double
)
