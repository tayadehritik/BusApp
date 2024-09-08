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

    @Query("Select * FROM CollectedRoute")
    fun getCollectedRouteOnce(): List<LatLngMarker>

    @Insert
    fun insertMarker(latLngMarkers:LatLngMarker)

    @Query("UPDATE CollectedRoute SET lat=:lat, lng=:lng WHERE tag=:tag")
    fun updateMarker(tag:Int, lat:Double, lng:Double)
    @Update
    fun updateAllMarkers(markers:List<LatLngMarker>)
    @Query("DELETE FROM CollectedRoute WHERE tag=:tag")
    fun deleteMarker(tag:Int)

    @Query("DELETE FROM CollectedRoute")
    fun clearRoute(): Int

}