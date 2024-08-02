package com.tayadehritik.busapp.ui.home


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Looper
import android.util.Xml
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tayadehritik.busapp.data.Bus
import com.tayadehritik.busapp.data.KMLHandler
import com.tayadehritik.busapp.data.OptimizedRoute
import com.tayadehritik.busapp.data.Route
import com.tayadehritik.busapp.data.Shape
import com.tayadehritik.busapp.data.User
import com.tayadehritik.busapp.data.local.AppDatabase
import com.tayadehritik.busapp.data.locationstuff.LocationClient
import com.tayadehritik.busapp.data.remote.BusNetwork
import com.tayadehritik.busapp.data.remote.GoogleRoadsAPI
import com.tayadehritik.busapp.data.remote.UserNetwork
import com.tayadehritik.busapp.ui.MainActivityCompose
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Properties
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val googleRoadsAPI: GoogleRoadsAPI,
    private val locationClient: LocationClient,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val xmlSerializer = Xml.newSerializer()
    lateinit var routeWatcherScope:CoroutineScope

    private val _searchViewActive = MutableStateFlow(false)
    val searchViewActive = _searchViewActive.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _fetchingRoutes = MutableStateFlow(true)
    val fetchingRoutes = _fetchingRoutes.asStateFlow()

    private val _recordingRoute = MutableStateFlow(false)
    val recordingRoute = _recordingRoute.asStateFlow()

    private val _routes = MutableStateFlow(listOf<Route>())
    val routes = _routes.asStateFlow()

    private var _currentRoute = MutableStateFlow<Route?>(null)
    val currentRoute = _currentRoute.asStateFlow()

    private var _currentMarker = MutableStateFlow<Int?>(null)
    val currentMarker = _currentMarker.asStateFlow()

    private var _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _coords = MutableStateFlow<List<LatLng>>(listOf())
    val coords = _coords.asStateFlow()

    private val busNetwork:BusNetwork = BusNetwork(Firebase.auth.currentUser!!.uid)
    private var allRoutes:List<Route> = listOf<Route>()


    init {
        viewModelScope.launch {
            _user.value  = UserNetwork.getUser()
            if(_user.value != null){
                if(_user.value!!.is_travelling){

                }
            }
            else {
                UserNetwork.addUser()
            }
        }
    }

    fun updateSearchViewActive(value:Boolean) {
        _searchViewActive.value = value
    }

    fun updateCurrentMarker(value:Int) {
        _currentMarker.value = value
    }


    fun updateSearchQuery(value:String) {
        _searchQuery.value = value
        _routes.value = allRoutes
        val allWords = value.split(" ")
        _routes.value = _routes.value.filter {route ->
            allWords.any { word ->
                route.route_no.lowercase().contains(word.lowercase()) or route.route.lowercase().contains(word.lowercase())
            }
        }
    }

    fun fetchAllRoutes() {
        if(fetchingRoutes.value) {
            viewModelScope.launch {
                _routes.value = busNetwork.getAllRoutes()
                allRoutes = _routes.value
                _fetchingRoutes.value = false
            }
        }
    }

    fun updateRecordingRoute(value:Boolean) {
        _recordingRoute.value = value
    }

    fun updateCurrentRoute(value: Route) {
        updateSearchQuery("${value.route_no} ${capitalizeWord(value.route)}")
        _currentRoute.value = value
        /*viewModelScope.launch {
            _currentBusShape.value = busNetwork.getBusShape(value)
            _fetchingRoute.value = false
        }*/
    }

    fun capitalizeWord(input:String):String {
        return input.split(" ").joinToString(" ") {
            it.lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    fun startLocationUpdates() {
        routeWatcherScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        appDatabase.LatLngMarkerDAO().getCollectedRoute()
            .onStart {

            }
            .onEach {
                _coords.value = it.map {
                    LatLng(it.lat,it.lng)
                }
            }
            .launchIn(routeWatcherScope)
    }

    fun stopRecordingRoute() {
        routeWatcherScope.cancel()
    }

    fun clearCoords() {
        CoroutineScope(Dispatchers.IO).launch {
            appDatabase.LatLngMarkerDAO().clearRoute()
            _coords.value = appDatabase.LatLngMarkerDAO().getCollectedRoute().first().map { LatLng(it.lat,it.lng) }
        }
    }
   fun optimizeRoute() {
        println(_coords.value)
        viewModelScope.launch {

            val mapsAPIKey = UserNetwork.getMapsAPIKey()
            val chunkedCoords = _coords.value.chunked(100)
            val completelist = mutableListOf<List<LatLng>>()
            for (chunk in chunkedCoords)
            {
                val path = chunk.map {
                    "${it.latitude},${it.longitude}"
                }.joinToString(separator = "|")


                val optimizedRoute: OptimizedRoute? = mapsAPIKey?.let { googleRoadsAPI.getOptimizedRoute(path,"false", it).body() }
                if (optimizedRoute != null) {
                    val optimizedChunk = optimizedRoute.snappedPoints.map {
                        LatLng(it.location.latitude,it.location.longitude)
                    }
                    completelist.add(optimizedChunk)
                }

            }

            _coords.value = completelist.flatten()

        }
    }


    fun exportKMLFile() {

    }


}