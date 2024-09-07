package com.tayadehritik.busapp.ui.home


import android.app.Application
import android.util.Xml
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.MarkerState
import com.tayadehritik.busapp.data.KMLHandler
import com.tayadehritik.busapp.data.MapState
import com.tayadehritik.busapp.data.OptimizedRoute
import com.tayadehritik.busapp.data.Route
import com.tayadehritik.busapp.data.User
import com.tayadehritik.busapp.data.local.AppDatabase
import com.tayadehritik.busapp.data.local.LatLngMarker
import com.tayadehritik.busapp.data.remote.BusNetwork
import com.tayadehritik.busapp.data.remote.GoogleRoadsAPI
import com.tayadehritik.busapp.data.remote.UserNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val googleRoadsAPI: GoogleRoadsAPI,
    private val kmlHandler: KMLHandler,
    private val appDatabase: AppDatabase,
    private val appContext: Application
) : ViewModel() {

    private val dao = appDatabase.routeCollectionDAO()
    private val _mapState = MutableStateFlow<List<LatLngMarker>>(listOf())
    val mapState = _mapState.asStateFlow()

    init {

        viewModelScope.launch {
            clearmap()
            dao.getCollectedRoute().collectLatest{
                _mapState.value = it
            }

        }
    }
    fun addMarker(id:Int,coord:LatLng){
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertMarker(LatLngMarker(id= id,lat = coord.latitude,lng = coord.longitude))
        }
    }

    fun updateMarker(id:Int, coords:LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            val latLngMarker = LatLngMarker(id, coords.latitude, coords.longitude)
            dao.updateMarker(latLngMarker)
        }
    }

    fun deleteMarker(id:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteMarker(id)
        }
    }
    fun clearmap() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearRoute()
        }
    }

}