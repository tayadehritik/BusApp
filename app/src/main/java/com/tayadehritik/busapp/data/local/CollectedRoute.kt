package com.tayadehritik.busapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState

@Entity(tableName = "CollectedRoute")
data class LatLngMarker(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    @ColumnInfo(name="tag") var tag:Int,
    @ColumnInfo(name = "lat") var lat: Double,
    @ColumnInfo(name="lng") var lng: Double
)
