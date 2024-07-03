package com.tayadehritik.busapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Route(val schedule:String, val route_no:String, val route:String)

@Serializable
data class Routes(val routes:List<Route>)