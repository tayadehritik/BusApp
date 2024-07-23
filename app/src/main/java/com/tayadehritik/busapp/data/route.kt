package com.tayadehritik.busapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Route(val schedule:String, val route_no:String, val route:String)

@Serializable
data class Routes(val routes:List<Route>)


data class OptimizedRoute(
    val snappedPoints: List<SnappedPoint>,
)

data class SnappedPoint(
    val location: Location,
    val originalIndex: Int,
    val placeId: String,
)

data class Location(
    val latitude: Double,
    val longitude: Double,
)
