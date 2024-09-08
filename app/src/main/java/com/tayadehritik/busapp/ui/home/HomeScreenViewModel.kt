package com.tayadehritik.busapp.ui.home


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.tayadehritik.busapp.data.local.AppDatabase
import com.tayadehritik.busapp.data.local.LatLngMarker
import com.tayadehritik.busapp.data.remote.GoogleRoadsAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val googleRoadsAPI: GoogleRoadsAPI,
    private val appDatabase: AppDatabase,
    private val appContext: Application
) : ViewModel() {

    private val dao = appDatabase.routeCollectionDAO()
    private val _mapState = MutableStateFlow<List<LatLng>>(listOf())
    val mapState = _mapState.asStateFlow()

    init {

        viewModelScope.launch {
            clearmap()
            dao.getCollectedRoute().collectLatest{
                _mapState.value = it.map { LatLng(it.lat,it.lng) }
            }

        }
    }
    fun addMarker(coord:LatLng){
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertMarker(LatLngMarker(id= 0,lat = coord.latitude,lng = coord.longitude))
        }
    }

    fun updateMarker(id:Int, coords:LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            val latLngMarker = LatLngMarker(id, coords.latitude, coords.longitude)
            dao.updateMarker(latLngMarker)
        }
    }

    fun deleteMarker(coord:LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteMarker(coord.latitude,coord.longitude)
        }
    }
    fun clearmap() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearRoute()
        }
    }

}