package com.tayadehritik.busapp.service

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.tayadehritik.busapp.R
import com.tayadehritik.busapp.ui.common.CHANNEL_ID
import com.tayadehritik.busapp.ui.common.NOTIFICATION_ID

class NavigationService: Service(), LocationListener {

    val serviceContext = this

    private fun startForeground() {

        try {

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.directions_bus)
                .setContentTitle("textTitle")
                .setContentText("textContent")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                notification.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            }


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
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {

    }


}