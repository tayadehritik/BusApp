package com.tayadehritik.busapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface RouteCollectionDAO {
    @Query("Select * FROM CollectedRoute")
    fun getCollectedRoute(): Flow<List<LatLngMarker>>

    @Insert
    fun insertMarker(latLngMarkers:LatLngMarker)

    @Update
    fun updateMarker(marker:LatLngMarker)
    @Query("DELETE FROM CollectedRoute WHERE id=:id")
    fun deleteMarker(id:Int)

    @Query("DELETE FROM CollectedRoute")
    fun clearRoute(): Int

}