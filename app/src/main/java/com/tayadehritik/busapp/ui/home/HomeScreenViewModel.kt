package com.tayadehritik.busapp.ui.home


import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tayadehritik.busapp.data.Bus
import com.tayadehritik.busapp.data.Route
import com.tayadehritik.busapp.data.Shape
import com.tayadehritik.busapp.data.User
import com.tayadehritik.busapp.data.remote.BusNetwork
import com.tayadehritik.busapp.data.remote.UserNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel: ViewModel() {

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

    private var _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()


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

}