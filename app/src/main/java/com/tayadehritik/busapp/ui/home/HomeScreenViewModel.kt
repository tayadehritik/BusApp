package com.tayadehritik.busapp.ui.home


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Looper
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
import com.tayadehritik.busapp.data.OptimizedRoute
import com.tayadehritik.busapp.data.Route
import com.tayadehritik.busapp.data.Shape
import com.tayadehritik.busapp.data.User
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
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val googleRoadsAPI: GoogleRoadsAPI
) : ViewModel() {

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

    private val mutableCoords = mutableListOf<LatLng>()
    private val _coords = MutableStateFlow<List<LatLng>>(listOf())
    val coords = _coords.asStateFlow()


    private val busNetwork:BusNetwork = BusNetwork(Firebase.auth.currentUser!!.uid)
    private var allRoutes:List<Route> = listOf<Route>()

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

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

    fun deleteCurrentMarker() {
        currentMarker.value?.let { mutableCoords.removeAt(it) }
        _coords.value = mutableCoords.toList()
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

    fun updateCoords(value:LatLng) {
        mutableCoords.add(value)
        _coords.value = mutableCoords.toList()
    }

    fun clearCoords() {
        mutableCoords.clear()
        _coords.value = mutableCoords.toList()
    }

   fun optimizeRoute() {

        viewModelScope.launch {
            val path = _coords.value.map {
                "${it.latitude},${it.longitude}"
            }.joinToString(separator = "|")
            println(path)

            /*val response: HttpResponse = client.get("https://roads.googleapis.com/v1/snapToRoads") {
                url {
                    parameters.append("path",path)
                    parameters.append("interpolate", "false")
                    parameters.append("key","AIzaSyA1KAVwH_VgOIpg-zSKYoS-0hs-B4WITEU")
                }
            }

            println(response.body() as String)*/
            val optimizedRoute: OptimizedRoute? = googleRoadsAPI.getOptimizedRoute(path,"false","AIzaSyA1KAVwH_VgOIpg-zSKYoS-0hs-B4WITEU").body()

            if (optimizedRoute != null) {
                println(optimizedRoute)
                _coords.value = optimizedRoute.snappedPoints.map {
                    LatLng(it.location.latitude,it.location.longitude)
                }
            }
        }

    }


}