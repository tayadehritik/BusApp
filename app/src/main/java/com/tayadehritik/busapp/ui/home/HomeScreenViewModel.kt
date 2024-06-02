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
import com.tayadehritik.busapp.data.Shape
import com.tayadehritik.busapp.data.User
import com.tayadehritik.busapp.data.remote.BusNetwork
import com.tayadehritik.busapp.data.remote.UserNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel: ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _fetchingBuses = MutableStateFlow(true)
    val fetchingBuses = _fetchingBuses.asStateFlow()

    private val _fetchingRoute = MutableStateFlow(false)
    val fetchingRoute = _fetchingRoute.asStateFlow()

    private val _buses = MutableStateFlow(listOf<Bus>())
    val buses = _buses.asStateFlow()

    private var _currentBus = MutableStateFlow<Bus?>(null)
    val currentBus = _currentBus.asStateFlow()

    private var _currentBusShape = MutableStateFlow<List<Shape>?>(null)
    val currentBusShape = _currentBusShape.asStateFlow()

    private var _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()


    private val busNetwork:BusNetwork = BusNetwork(Firebase.auth.currentUser!!.uid)
    private var allBuses:List<Bus> = listOf<Bus>()

    init {
        viewModelScope.launch {
            _user.value  = UserNetwork.getUser()
            if(_user.value != null){
                if(_user.value!!.is_travelling){
                    updateCurrentBus(Bus(
                        _user.value!!.route_id,
                        _user.value!!.route_short_name,
                        _user.value!!.shape_id,
                        _user.value!!.trip_headsign)
                    )
                }
            }
            else {
                UserNetwork.addUser()
            }
        }
    }

    fun updateSearchQuery(value:String) {
        _searchQuery.value = value
        _buses.value = allBuses
        val allWords = value.split(" ")
        _buses.value = _buses.value.filter {bus ->
            allWords.any { word ->
                bus.route_short_name.lowercase().contains(word.lowercase()) or bus.trip_headsign.lowercase().contains(word.lowercase())
            }
        }
    }

    fun fetchAllBuses() {
        if(fetchingBuses.value) {
            viewModelScope.launch {
                _buses.value = busNetwork.allBuses()
                allBuses = _buses.value
                _fetchingBuses.value = false
            }
        }
    }

    fun updateCurrentBus(value: Bus) {
        updateSearchQuery("${value.route_short_name} ${value.trip_headsign}")
        _currentBus.value = value
        _fetchingRoute.value = true
        viewModelScope.launch {
            _currentBusShape.value = busNetwork.getBusShape(value)
            _fetchingRoute.value = false
        }
    }

}