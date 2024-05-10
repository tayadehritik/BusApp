package com.tayadehritik.busapp.ui.home


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Insets
import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemGesturesPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import com.google.maps.android.clustering.algo.GridBasedAlgorithm
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.ui.MainActivityCompose
import com.tayadehritik.busapp.ui.common.LoadingDialog
import com.tayadehritik.busapp.ui.common.PermissionBox
import com.tayadehritik.busapp.ui.list_items.BusItem

private val viewModel:HomeScreenViewModel = HomeScreenViewModel()

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen()
{

    val activityContext = LocalContext.current as MainActivityCompose

    val mapStyleOptions =
        if(isSystemInDarkTheme())
            MapStyleOptions.loadRawResourceStyle(LocalContext.current,R.raw.style_json_dark)
        else
            MapStyleOptions.loadRawResourceStyle(LocalContext.current,R.raw.style_json_light)

    val searchQuery by viewModel.searchQuery.collectAsState()
    val fetchingBuses by viewModel.fetchingBuses.collectAsState()
    val buses by viewModel.buses.collectAsState()
    var searchViewActive by remember { mutableStateOf(false) }

    val activityPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Manifest.permission.ACTIVITY_RECOGNITION
    } else {
        "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
    }


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                SearchBar(
                    colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    shadowElevation = 6.dp,
                    leadingIcon = {
                        if(!searchViewActive)
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
                        if(searchViewActive)
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Clear Search")
                            }
                    },
                    placeholder = {Text("Search by bus no or destination")},
                    query = searchQuery,
                    onQueryChange = {
                        viewModel.updateSearchQuery(it)
                    },
                    onSearch = {
                        //filter through list
                    },
                    active = searchViewActive,
                    onActiveChange = {
                        searchViewActive =  !searchViewActive
                        if(searchViewActive && buses.isEmpty()) viewModel.fetchAllBuses()
                    }
                ) {
                    AnimatedVisibility(visible = fetchingBuses) {
                        LoadingDialog(text = "Fetching buses")
                    }

                    LazyColumn {
                        items(buses.size) {
                            BusItem(
                                buses[it].route_short_name,
                                buses[it].trip_headsign
                            )
                        }
                    }

                }
            }

        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {

                    //permissionBox.askPermission()
                }
            ) {
                Text(text = "Start listening for activities")
            }
        }
    ) { contentPadding  ->

        println(contentPadding)
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                properties = MapProperties(mapStyleOptions = mapStyleOptions),
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    zoomControlsEnabled = false)
            ) {

            }

            PermissionBox(permissions = listOf(activityPermission)) {
                println("Permission Granted in HomeScreen")
            }

        }

    }

}