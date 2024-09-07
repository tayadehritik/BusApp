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
import com.google.firebase.auth.PhoneAuthCredential
import com.tayadehritik.busapp.ui.authentication.LoginOrSignUp
import com.tayadehritik.busapp.ui.authentication.LoginOrSignUpScreen
import com.tayadehritik.busapp.ui.authentication.LoginOrSignUpViewModel
import com.tayadehritik.busapp.ui.authentication.VerificationScreen
import com.tayadehritik.busapp.ui.authentication.VerificationScreenViewModel
import com.tayadehritik.busapp.ui.home.HomeScreen
import com.tayadehritik.busapp.ui.home.HomeScreenViewModel


@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination:String
) {
    val loginOrSignUpViewModel = hiltViewModel<LoginOrSignUpViewModel>()
    val verificationScreenViewModel = hiltViewModel<VerificationScreenViewModel>()


    NavHost(navController = navController,startDestination) {



        composable("LogInOrSignUpScreen") {


            LoginOrSignUpScreen(
                onNavigateToVerificationScreen = { verificationId ->
                    navController.navigate("VerificationScreen/$verificationId")
                },
                onNavigateToHomeScreen = {
                    navController.navigate("HomeScreen")
                },
                loginOrSignUpViewModel
            )

        }

        composable("VerificationScreen/{verificationId}",
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }


        ) {backStackEntry ->
            BackHandler(true) {

            }

            VerificationScreen(
                backStackEntry.arguments?.getString("verificationId"),
                onNavigateToHomeScreen = {
                    navController.navigate("HomeScreen")
                },
                verificationScreenViewModel
            )
        }

        composable("HomeScreen") {
            val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(homeScreenViewModel)
        }
    }
}