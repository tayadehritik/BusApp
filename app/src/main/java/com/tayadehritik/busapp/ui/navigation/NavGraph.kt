package com.tayadehritik.busapp.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tayadehritik.busapp.ui.home.HomeScreen
import com.tayadehritik.busapp.ui.home.HomeScreenViewModel


@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination:String
) {

    NavHost(navController = navController,startDestination) {

        composable("HomeScreen") {
            val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(homeScreenViewModel)
        }
    }
}