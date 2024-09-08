package com.tayadehritik.busapp.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline


@Preview
@Composable
fun MapEditor(
    markerCoords:List<LatLng> = listOf(),
    addMarker: (LatLng) -> Unit = {},
    deleteMarker: (LatLng) -> Unit = {}
) {
    var showMarkerOptions by remember { mutableStateOf(false) }
    var selectedMarker by remember { mutableStateOf<LatLng>( LatLng(0.0,0.0)) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AnimatedVisibility(visible = showMarkerOptions && markerCoords.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ExtendedFloatingActionButton(
                        icon = { Icon(Icons.Default.Edit, contentDescription = "Add")},
                        text = { Text(text = "Edit") },
                        onClick = { /*TODO*/ }
                    )
                    ExtendedFloatingActionButton(
                        icon = { Icon(Icons.Default.Delete, contentDescription = "Add") },
                        text = { Text(text = "Delete") },
                        onClick = { deleteMarker(selectedMarker) }
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Start
    ) { paddingValues ->
        println(paddingValues)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            onMapClick = { addMarker(it) }
        ){
            Polyline(points = markerCoords)
            markerCoords.forEach {coord ->
                MarkerInfoWindow(
                    state = MarkerState(coord),
                    onClick = {
                        showMarkerOptions = true
                        selectedMarker = coord
                        false
                    },
                    onInfoWindowClose = { showMarkerOptions = false }
                ) {
                    Text(text = "MarkerInfoWindow")
                }
            }
        }
    }
}