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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val googleRoadsAPI: GoogleRoadsAPI,
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
    fun addMarker(coord:LatLng){
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertMarker(LatLngMarker(tag=_mapState.value.size, lat = coord.latitude, lng = coord.longitude))
        }
    }

    fun updateMarker(tag:Int, coords:LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateMarker(tag, coords.latitude, coords.longitude)
        }
    }

    fun updateIndices() {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedMarkers = dao.getCollectedRouteOnce().mapIndexed {index, latLngMarker ->
                latLngMarker.tag = index
                latLngMarker
            }
            dao.updateAllMarkers(updatedMarkers)
        }
    }
    fun deleteMarker(tag:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteMarker(tag)
            updateIndices()
        }


    }
    fun clearmap() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearRoute()
        }
    }

}