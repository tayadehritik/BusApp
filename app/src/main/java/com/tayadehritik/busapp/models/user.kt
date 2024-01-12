package com.tayadehritik.busapp.models
import kotlinx.serialization.Serializable

@Serializable
data class User(val user_id:String, val is_travelling:Boolean, val bus:String, val route:String, val lat:Double, val long:Double)
