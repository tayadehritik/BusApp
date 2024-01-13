package com.tayadehritik.busapp.models

import kotlinx.serialization.Serializable


@Serializable
data class Route(val bus:String,val route:String)

@Serializable
data class Routes(val routes:List<Route>)
