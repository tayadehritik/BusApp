package com.tayadehritik.busapp.data

import com.google.android.gms.maps.model.LatLng

data class MapState(
    val markers: Map<Int,LatLng> = mapOf()
)
