package com.tayadehritik.busapp.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tayadehritik.busapp.data.Bus
import com.tayadehritik.busapp.data.remote.BusNetwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel: ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _fetchingBuses = MutableStateFlow(true)
    val fetchingBuses = _fetchingBuses.asStateFlow()

    private val _buses = MutableStateFlow(listOf<Bus>())
    val buses = _buses.asStateFlow()

    private val busNetwork:BusNetwork = BusNetwork(Firebase.auth.currentUser!!.uid)
    private var allBuses:List<Bus> = listOf<Bus>()

    fun updateSearchQuery(value:String) {
        _searchQuery.value = value
        _buses.value = allBuses
        _buses.value = _buses.value.filter { it.trip_headsign.contains(value) or it.route_short_name.contains(value) }
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

}