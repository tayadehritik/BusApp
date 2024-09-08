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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings


@Preview
@Composable
fun MapEditor() {
    var editingMarker by remember { mutableStateOf(true) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AnimatedVisibility(visible = editingMarker) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ExtendedFloatingActionButton(
                        icon = { Icon(Icons.Default.Edit, contentDescription = "Add")},
                        text = { Text(text = "Edit") },
                        onClick = { /*TODO*/ }
                    )
                    ExtendedFloatingActionButton(
                        icon = { Icon(Icons.Default.Delete, contentDescription = "Add") },
                        text = { Text(text = "Delete") },
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }
    ) {
        println(it)
        GoogleMap(modifier = Modifier.fillMaxSize(),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)){}
    }
}