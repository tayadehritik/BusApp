package com.tayadehritik.busapp.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import com.tayadehritik.busapp.ui.theme.BusAppTheme

import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.tayadehritik.busapp.ui.navigation.NavGraph


class MainActivityCompose : ComponentActivity() {


    private val mainViewModel:MainViewModel = MainViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
            BusAppTheme {
                    val startDestination by mainViewModel.startDestination.collectAsState()
                    val navController = rememberNavController()
                    NavGraph(navController = navController, startDestination = startDestination)
            }
        }
    }
}




