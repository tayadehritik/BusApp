package com.tayadehritik.busapp.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.PhoneAuthCredential
import com.tayadehritik.busapp.ui.authentication.LoginOrSignUp
import com.tayadehritik.busapp.ui.authentication.LoginOrSignUpScreen
import com.tayadehritik.busapp.ui.authentication.LoginOrSignUpViewModel
import com.tayadehritik.busapp.ui.authentication.VerificationScreen
import com.tayadehritik.busapp.ui.authentication.VerificationScreenViewModel
import com.tayadehritik.busapp.ui.home.HomeScreen


@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination:String
) {
    NavHost(navController = navController,startDestination) {

        composable("LogInOrSignUpScreen") {


            LoginOrSignUpScreen(
                onNavigateToVerificationScreen = { verificationId ->
                    navController.navigate("VerificationScreen/$verificationId")
                },
                onNavigateToHomeScreen = {
                    navController.navigate("HomeScreen")
                }
            )

        }

        composable("VerificationScreen/{verificationId}") {backStackEntry ->
            BackHandler(true) {

            }

            VerificationScreen(
                backStackEntry.arguments?.getString("verificationId"),
                onNavigateToHomeScreen = {
                    navController.navigate("HomeScreen")
                }
            )
        }

        composable("HomeScreen") {
            HomeScreen()
        }
    }
}