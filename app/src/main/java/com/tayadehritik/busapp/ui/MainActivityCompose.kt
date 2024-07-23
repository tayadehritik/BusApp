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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {


    private val mainViewModel:MainViewModel = MainViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate()")
        enableEdgeToEdge()
        setContent {
            BusAppTheme {
                    val startDestination by mainViewModel.startDestination.collectAsState()
                    val navController = rememberNavController()
                    NavGraph(navController = navController, startDestination = startDestination)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
    }

    override fun onPause() {
        super.onPause()
        println("onPause")
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
    }

}




