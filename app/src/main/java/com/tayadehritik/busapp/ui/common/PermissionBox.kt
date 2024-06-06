package com.tayadehritik.busapp.ui.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.tayadehritik.busapp.ui.MainActivityCompose

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionBox(
    permissions: List<String>,
    requiredPermissions: List<String> = permissions,
    onGranted: (List<String>) -> Unit,
) {
    val context = LocalContext.current
    var errorText by remember {
        mutableStateOf("")
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions) { map ->
        val rejectedPermissions = map.filterValues { !it }.keys
        errorText = if (rejectedPermissions.none { it in requiredPermissions }) {
            ""
        } else {
            "${rejectedPermissions.joinToString()} required for this feature to work"

        }

    }
    val allRequiredPermissionsGranted =
        permissionState.revokedPermissions.none { it.permission in requiredPermissions }


    if (allRequiredPermissionsGranted) {
        onGranted(
            permissionState.permissions
                .filter { it.status.isGranted }
                .map { it.permission },
        )
    } else {

        LaunchedEffect(Unit) {
            permissionState.launchMultiplePermissionRequest()
        }

    }

}