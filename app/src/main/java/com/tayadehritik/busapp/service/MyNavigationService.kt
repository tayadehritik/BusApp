package com.tayadehritik.busapp.service

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.Priority
import com.tayadehritik.busapp.R
import java.lang.Exception

class MyNavigationService: Service() {

    private lateinit var locationCallback:LocationCallback
    private lateinit var fusedLocationClient:FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }
    private fun startForeground() {
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(locationPermission == PackageManager.PERMISSION_DENIED)
        {

            println("location permission has not been granted, service cannot be started")
            stopSelf()
            return
        }
        println("location permission has been granted and service has started")
        try {
            val notification = NotificationCompat.Builder(this, "location")
                .setContentTitle("Tracking location of bus ..")
                .setContentText("location")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            //update location on server
            startForeground(1, notification)


        }
        catch (e:Exception) {
            println("something went wrong")
        }
        //create notification

    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}