package com.tayadehritik.busapp.ui.home


import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.data.Bus
import com.tayadehritik.busapp.service.NavigationService
import com.tayadehritik.busapp.ui.common.LoadingDialog
import com.tayadehritik.busapp.ui.common.PermissionBox
import com.tayadehritik.busapp.ui.list_items.BusItem
import com.tayadehritik.busapp.ui.list_items.TravellingOn

private val viewModel:HomeScreenViewModel = HomeScreenViewModel()

@Preview
@Composable
fun HomeScreen()
{
    val context = LocalContext.current

    val mapStyleOptions =
        if(isSystemInDarkTheme())
            MapStyleOptions.loadRawResourceStyle(context,R.raw.style_json_dark)
        else
            MapStyleOptions.loadRawResourceStyle(context,R.raw.style_json_light)


    val fetchingRoute by viewModel.fetchingRoute.collectAsState()
    val currentBusShape by viewModel.currentBusShape.collectAsState()
    val currentBus by viewModel.currentBus.collectAsState()
    val user by viewModel.user.collectAsState()


    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition(LatLng(18.5204, 73.8567), 12f, 0f,0f)
    }


    PermissionBox(permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.POST_NOTIFICATIONS)) {

    }

    Scaffold(
        topBar = {
            SearchViewCustom()
        },
        floatingActionButton = {
            if(user?.is_travelling == true){
                currentBus?.let {
                    TravellingOn(
                        routeShortName = it.route_short_name,
                        tripHeadSign = it.trip_headsign,
                        onCancel = {
                            //set user is travelling to false

                        }
                    )
                }
            }
            else {
                ExtendedFloatingActionButton(
                    onClick = {
                        //set user to travelling

                        val coarsePermission = ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)
                        val fineLocation = ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)
                        val notificationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)

                        if(coarsePermission == PackageManager.PERMISSION_GRANTED &&
                            fineLocation == PackageManager.PERMISSION_GRANTED &&
                            notificationPermission == PackageManager.PERMISSION_GRANTED) {
                            //have permissions
                            val intent = Intent(context,NavigationService::class.java)
                            try{
                                context.startForegroundService(intent)
                            }
                            catch(e:Exception) {
                                println("here")
                            }

                        }
                        else {
                            val toast = Toast.makeText(context, "One of the permissions is not granted", Toast.LENGTH_LONG)
                            toast.show()
                        }


                    },
                    icon = { Icon(painter = painterResource(id = R.drawable.directions_bus), "Start sharing location") },
                    text = { Text(text = "Are you travelling on this bus") },
                )
            }

        },
        floatingActionButtonPosition = FabPosition.Center
    ) { contentPadding  ->

        println(contentPadding)
        Box(modifier = Modifier.fillMaxSize()) {

            AnimatedVisibility(visible = fetchingRoute) {
                LoadingDialog(text = "Fetching route")
            }

            GoogleMap(
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapStyleOptions = mapStyleOptions),
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    zoomControlsEnabled = false)
            ) {
                if(currentBusShape != null)
                {
                    Polyline(points = currentBusShape!!.map { LatLng(it.shape_pt_lat.toDouble(), it.shape_pt_lon.toDouble()) })
                    val middleShape = currentBusShape!![currentBusShape!!.size/2]
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(middleShape.shape_pt_lat.toDouble(),middleShape.shape_pt_lon.toDouble()),12f)
                }
            }

        }

    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchViewCustom() {

    var searchViewActive by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val fetchingBuses by viewModel.fetchingBuses.collectAsState()
    val buses by viewModel.buses.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        SearchBar(
            colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shadowElevation = 6.dp,
            leadingIcon = {
                if (!searchViewActive)
                    Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search")
                else
                    IconButton(onClick = { searchViewActive = false }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
            },
            trailingIcon = {
                if (searchViewActive)
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Clear Search")
                    }
            },
            placeholder = { Text("Search by bus no or destination") },
            query = searchQuery,
            onQueryChange = {
                viewModel.updateSearchQuery(it)
            },
            onSearch = {
                //filter through list
            },
            active = searchViewActive,
            onActiveChange = {
                searchViewActive = !searchViewActive
                if (searchViewActive && buses.isEmpty()) viewModel.fetchAllBuses()
            }
        ) {
            AnimatedVisibility(visible = fetchingBuses) {
                LoadingDialog(text = "Fetching buses")
            }

            LazyColumn {
                items(buses.size) {
                    BusItem(
                        modifier = Modifier.clickable {
                            println("here")
                            viewModel.updateCurrentBus(buses[it])
                            searchViewActive = false
                        },
                        buses[it].route_short_name,
                        buses[it].trip_headsign
                    )
                }
            }

        }
    }
}