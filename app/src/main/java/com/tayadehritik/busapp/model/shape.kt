package com.tayadehritik.busapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Shape(val shape_id: String, val shape_pt_lat:String, val shape_pt_lon:String, val shape_pt_sequence:Int,val shape_dist_traveled:String?)

@Serializable
data class BusShape(val busShape:List<Shape>)