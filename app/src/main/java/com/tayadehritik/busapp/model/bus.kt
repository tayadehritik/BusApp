package com.tayadehritik.busapp.model

import kotlinx.serialization.Serializable


@Serializable
data class Bus(val route_id:String, val route_short_name:String, val shape_id:String, val trip_headsign:String)

@Serializable
data class Buses(val buses:List<Bus>)
