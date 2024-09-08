package com.tayadehritik.busapp.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.data.local.AppDatabase
import com.tayadehritik.busapp.data.local.LatLngMarker
import com.tayadehritik.busapp.data.locationstuff.LocationClient
import com.tayadehritik.busapp.ui.MainActivityCompose
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NavigationService: Service(){
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}