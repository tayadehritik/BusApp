package com.tayadehritik.busapp.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.data.local.AppDatabase
import com.tayadehritik.busapp.data.local.LatLngMarker
import com.tayadehritik.busapp.data.locationstuff.LocationClient
import com.tayadehritik.busapp.ui.MainActivityCompose
import com.tayadehritik.busapp.ui.common.ACTION_START
import com.tayadehritik.busapp.ui.common.ACTION_STOP
import com.tayadehritik.busapp.ui.common.CHANNEL_ID
import com.tayadehritik.busapp.ui.common.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NavigationService: Service(){

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Inject
    lateinit var locationClient: LocationClient
    @Inject
    lateinit var appDatabase: AppDatabase


    private fun startForeground() {

        try {

            val mainActivityIntent = Intent(this, MainActivityCompose::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val mainActivityPendingIntent:PendingIntent = PendingIntent.getActivity(this,1,mainActivityIntent,PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.directions_bus)
                .setContentTitle("Recording Route")
                .setContentText("100 UP - MaNaPa to Hinjewadi")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(mainActivityPendingIntent)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                notification.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            var position = 0
            serviceScope.launch { appDatabase.LatLngMarkerDAO().clearRoute() }

            locationClient.getLocationUpdates(10000L)
                .catch { e -> e.printStackTrace() }
                .onEach { location ->
                    val lat = location.latitude.toString()
                    val long = location.longitude.toString()
                    val updatedNotification = notification.setContentText(
                        "Location: ($lat, $long)"
                    )
                    notificationManager.notify(NOTIFICATION_ID,updatedNotification.build())
                    appDatabase.LatLngMarkerDAO().insertMarker(LatLngMarker(position,lat = location.latitude,lng = location.longitude))
                    position++
                }
                .launchIn(serviceScope)

            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )

            /*with(NotificationManagerCompat.from(this)) {
                if (ContextCompat.checkSelfPermission(
                        serviceContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    // ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                    //                                        grantResults: IntArray)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    println("Notification Permission not granted")
                    return@with
                }
                // notificationId is a unique int for each notification that you must define.
                //notify(NOTIFICATION_ID, notification.build())
            }*/


        }
        catch (e:Exception) {
            println("something went wrong")
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if(action.equals(ACTION_START)) {
            startForeground()
        }
        else if(action.equals(ACTION_STOP)) {
            stopForeground(true)
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }




}