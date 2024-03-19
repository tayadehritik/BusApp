package com.tayadehritik.busapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tayadehritik.busapp.ui.authentication.LoginOrSignUp
import com.tayadehritik.busapp.ui.authentication.LoginOrSignUpScreen
import com.tayadehritik.busapp.ui.authentication.LoginOrSignUpViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination:String =  "LogInOrSignUp"
) {
    NavHost(navController = navController,startDestination) {

        composable("LogInOrSignUp") {
            LoginOrSignUpScreen(
                viewModel = LoginOrSignUpViewModel(navController)
            )
        }

    }
}