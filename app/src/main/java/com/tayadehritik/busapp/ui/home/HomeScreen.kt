package com.tayadehritik.busapp.ui.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.tayadehritik.busapp.R


@Preview
@Composable
fun HomeScreen()
{
    var mapStyleOptions = MapStyleOptions.loadRawResourceStyle(LocalContext.current,R.raw.style_json_light)

    if(isSystemInDarkTheme())
    {
        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(LocalContext.current,R.raw.style_json_dark)

    }

    Box(modifier = Modifier.fillMaxSize())
    {
        GoogleMap(
            properties = MapProperties(
                mapStyleOptions = mapStyleOptions

            ),
            uiSettings = MapUiSettings(
                compassEnabled = false,
                zoomControlsEnabled = false
            )
        ) {

        }

    }


    /*Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,

    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello World"
            )
        }

    }*/

}