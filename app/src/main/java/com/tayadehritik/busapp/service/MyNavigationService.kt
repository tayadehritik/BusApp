package com.tayadehritik.busapp.service

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.models.Bus
import com.tayadehritik.busapp.models.User
import com.tayadehritik.busapp.network.UserNetwork
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class MyNavigationService: Service() {



    val STOPACTION = "stop";
    val STARTACTION = "start"


    private lateinit var auth: FirebaseAuth
    var userNetwork: UserNetwork? = null


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest:LocationRequest

    private lateinit var bus: Bus

    private var job1:Job? = null
    private var job2:Job? = null


    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        userNetwork = UserNetwork(auth.currentUser!!.uid)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
            .build()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(intent?.action == STARTACTION)
        {
            bus = Bus(intent!!.getStringExtra("bus")!!,intent.getStringExtra("route")!!,"test","test")

            job1 = GlobalScope.launch {
                try {
                    userNetwork!!.openUserUpdateConnection()
                }
                finally {
                    userNetwork!!.stopUserUpdateConnection()
                }
            }
            startForeground()
        }
        else if(intent?.action == STOPACTION)
        {

            stopForeground(true)

            stopSelf()

        }


        return super.onStartCommand(intent, flags, startId)
    }
    private fun startForeground() {

        val stopIntent = Intent(this, MyNavigationService::class.java).apply {
            action = STOPACTION
        }

        val stopPendingIntent:PendingIntent = PendingIntent.getService(this,0,stopIntent, PendingIntent.FLAG_IMMUTABLE)

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
                .addAction(R.drawable.ic_launcher_background, "STOP", stopPendingIntent)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            //open websocket connection

            //get device location lat long
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult ?: return
                    for (location in locationResult.locations){
                        // Update UI with location data
                        notificationManager.apply {
                            notification.setContentText("lat: ${location.latitude} long: ${location.longitude}")
                            notify(1,notification.build())
                        }
                        val user = User(auth.currentUser!!.uid,true,bus.route_short_name,bus.trip_headsign, location.latitude,location.longitude)
                        //update location on server
                        GlobalScope.launch {
                            userNetwork?.updateUser(user);
                        }

                    }

                }
            }



            startLocationUpdates()
            startForeground(1, notification.build())


        }
        catch (e:Exception) {
            println("something went wrong")
        }
        //create notification

    }

    private fun startLocationUpdates() {
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if(locationPermission == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }

    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }



    override fun onDestroy() {
        println("service is being stopped")
        val user = User(auth.currentUser!!.uid,false,bus.route_short_name,bus.trip_headsign, 0.0,0.0)
        //update location on server
        GlobalScope.launch {

            userNetwork?.updateUser(user);

        }

        job1!!.cancel()
        stopLocationUpdates()
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}