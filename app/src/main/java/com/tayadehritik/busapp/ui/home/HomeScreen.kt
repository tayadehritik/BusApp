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
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toLowerCase
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.data.Bus
import com.tayadehritik.busapp.service.NavigationService
import com.tayadehritik.busapp.ui.MainActivityCompose
import com.tayadehritik.busapp.ui.common.ACTION_START
import com.tayadehritik.busapp.ui.common.ACTION_STOP
import com.tayadehritik.busapp.ui.common.LoadingDialog
import com.tayadehritik.busapp.ui.common.PermissionBox
import com.tayadehritik.busapp.ui.list_items.BusItem
import com.tayadehritik.busapp.ui.list_items.TravellingOn


private val viewModel:HomeScreenViewModel = HomeScreenViewModel()
private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var locationCallback: LocationCallback
private lateinit var locationRequest: LocationRequest
private lateinit var context:Context
@SuppressLint("MissingPermission")
private fun startLocationUpdates() {
    viewModel.clearCoords()
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000).build()
    locationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for(location in locationResult.locations) {

                viewModel.updateCoords(LatLng(location.latitude,location.longitude))
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}

private fun stopLocationUpdates() {

    fusedLocationClient.removeLocationUpdates(locationCallback)
}

@Preview
@Composable
fun HomeScreen()
{
    context = LocalContext.current

    val mapStyleOptions =
        if(isSystemInDarkTheme())
            MapStyleOptions.loadRawResourceStyle(context,R.raw.style_json_dark)
        else
            MapStyleOptions.loadRawResourceStyle(context,R.raw.style_json_light)


    //val fetchingRoute by viewModel.fetchingRoute.collectAsState()
    //val currentBusShape by viewModel.currentBusShape.collectAsState()
    val currentRoute by viewModel.currentRoute.collectAsState()
    val user by viewModel.user.collectAsState()
    val recordingRoute by viewModel.recordingRoute.collectAsState()
    val coords by viewModel.coords.collectAsState()


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
                /*currentBus?.let {
                    TravellingOn(
                        routeShortName = it.route_short_name,
                        tripHeadSign = it.trip_headsign,
                        onCancel = {
                            //set user is travelling to false

                        }
                    )
                }*/
            }
            else {
                if (currentRoute != null) {
                    if(!recordingRoute) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                //set user to travelling

                                val coarsePermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                                val fineLocation = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                                val notificationPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )

                                if (coarsePermission == PackageManager.PERMISSION_GRANTED &&
                                    fineLocation == PackageManager.PERMISSION_GRANTED &&
                                    notificationPermission == PackageManager.PERMISSION_GRANTED
                                ) {
                                    //have permissions
                                    startLocationUpdates()
                                    val intent = Intent(context, NavigationService::class.java)
                                        .apply {
                                            action = ACTION_START
                                        }
                                    try {
                                        context.startForegroundService(intent)
                                        viewModel.updateRecordingRoute(true)
                                    } catch (e: Exception) {
                                        println("here")
                                    }

                                } else {
                                    val toast = Toast.makeText(
                                        context,
                                        "One of the permissions required for this feature is not granted",
                                        Toast.LENGTH_LONG
                                    )
                                    toast.show()
                                }


                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.directions_bus),
                                    "Start recording this route"
                                )
                            },
                            text = { Text(text = "Start recording this route") },
                        )
                    }
                    else{
                        ExtendedFloatingActionButton(
                            onClick = {
                                //set user to travelling

                                val coarsePermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                                val fineLocation = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                                val notificationPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )

                                if (coarsePermission == PackageManager.PERMISSION_GRANTED &&
                                    fineLocation == PackageManager.PERMISSION_GRANTED &&
                                    notificationPermission == PackageManager.PERMISSION_GRANTED
                                ) {
                                    //have permissions
                                    stopLocationUpdates()
                                    val intent = Intent(context, NavigationService::class.java)
                                        .apply {
                                            action = ACTION_STOP
                                        }
                                    try {
                                        context.startForegroundService(intent)
                                        viewModel.updateRecordingRoute(false)
                                    } catch (e: Exception) {
                                        println("here")
                                    }

                                } else {
                                    val toast = Toast.makeText(
                                        context,
                                        "One of the permissions required for this feature is not granted",
                                        Toast.LENGTH_LONG
                                    )
                                    toast.show()
                                }


                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.directions_bus),
                                    "Start recording this route"
                                )
                            },
                            text = { Text(text = "Stop recording this route") },
                        )
                    }
                }
            }

        },
        floatingActionButtonPosition = FabPosition.Center
    ) { contentPadding  ->

        println(contentPadding)
        Box(modifier = Modifier.fillMaxSize()) {

            /*AnimatedVisibility(visible = fetchingRoute) {
                LoadingDialog(text = "Fetching route")
            }*/

            GoogleMap(
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapStyleOptions = mapStyleOptions),
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    zoomControlsEnabled = false)
            ) {
                if(recordingRoute and coords.isNotEmpty())
                {

                    Polyline(points = coords )
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(coords.last(),12f)
                }
            }

        }

    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchViewCustom() {
    val context = LocalContext.current
    var searchViewActive by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val fetchingRoutes by viewModel.fetchingRoutes.collectAsState()
    val routes by viewModel.routes.collectAsState()
    val recordingRoute by viewModel.recordingRoute.collectAsState()

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
            placeholder = { Text("Search by route no or route") },
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
                if (searchViewActive && routes.isEmpty()) viewModel.fetchAllRoutes()
            }
        ) {
            AnimatedVisibility(visible = fetchingRoutes) {
                LoadingDialog(text = "Fetching routes")
            }

            LazyColumn {
                items(routes.size) {
                    BusItem(
                        modifier = Modifier.clickable {
                            if(!recordingRoute) {
                                viewModel.updateCurrentRoute(routes[it])
                                searchViewActive = false
                            }
                            else {
                                val toastMessage = Toast.makeText(context,"Please stop recording the current route to select a new route", Toast.LENGTH_SHORT)
                                toastMessage.show()
                            }
                        },
                        routes[it].route_no,
                        viewModel.capitalizeWord( routes[it].route)
                    )
                }
            }

        }
    }
}


