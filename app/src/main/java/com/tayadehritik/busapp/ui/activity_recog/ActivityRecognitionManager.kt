package com.tayadehritik.busapp.ui.activity_recog

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.tayadehritik.busapp.data.USER_ACTIVITY
import com.tayadehritik.busapp.data.USER_ACTIVITY_REQUEST_CODE
import kotlinx.coroutines.tasks.await

class ActivityRecognitionManager(context: Context) {
    private val activityClient = ActivityRecognition.getClient(context)

    private val activityTransitionList: List<ActivityTransition> = listOf(
        ActivityTransition.Builder()
            .setActivityType(DetectedActivity.IN_VEHICLE)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            .build(),
        ActivityTransition.Builder()
            .setActivityType(DetectedActivity.IN_VEHICLE)
            .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
            .build()
    )

    private val request = ActivityTransitionRequest(activityTransitionList)

    private val pendingIntent = PendingIntent.getBroadcast(
        context,
        USER_ACTIVITY_REQUEST_CODE,
        Intent(USER_ACTIVITY),
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            PendingIntent.FLAG_CANCEL_CURRENT
        } else {
            PendingIntent.FLAG_MUTABLE
        }
    )

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACTIVITY_RECOGNITION,
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        ]
    )
    fun registerActivityTransitions() {
        val task = activityClient.requestActivityTransitionUpdates(
            request, pendingIntent
        )
        /*task.addOnSuccessListener {
            println("Successfully registered")
        }
        task.addOnFailureListener {
            println("Failed to register")
        }*/
    }


    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACTIVITY_RECOGNITION,
            "com.google.android.gms.permission.ACTIVITY_RECOGNITION"
        ]
    )
    suspend fun deregisterActivityTransitions() = kotlin.runCatching {
        activityClient.removeActivityUpdates(pendingIntent).await()
    }
}