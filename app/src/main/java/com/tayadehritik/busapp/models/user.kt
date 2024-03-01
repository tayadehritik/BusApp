package com.tayadehritik.busapp.models
import kotlinx.serialization.Serializable

@Serializable
data class User(val user_id:String, val is_travelling:Boolean, val lat:Double, val long:Double, val route_id:String, val route_short_name:String, val shape_id:String, val trip_headsign:String)

@Serializable
data class Users(val users:List<User>)