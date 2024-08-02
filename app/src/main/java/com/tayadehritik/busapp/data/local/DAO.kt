package com.tayadehritik.busapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LatLngMarkerDAO {
    @Query("Select * FROM CollectedRoute")
    fun getCollectedRoute(): Flow<List<LatLngMarker>>

    @Insert
    fun insertMarker(vararg latLngMarkers:LatLngMarker)

    @Query("DELETE FROM collectedroute")
    fun clearRoute(): Int
}