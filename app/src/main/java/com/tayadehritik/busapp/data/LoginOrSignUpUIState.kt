package com.tayadehritik.busapp.data

data class LoginOrSignUpUIState(
    var phoneNumber:String = "",
    var isError:Boolean = false,
    var supportingText:String = "Log in or sign up",
    var loginStarted:Boolean = false,
    var loadingMessage:String = "Sending OTP"
)

