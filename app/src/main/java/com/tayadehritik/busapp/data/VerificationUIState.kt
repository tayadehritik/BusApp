package com.tayadehritik.busapp.data

data class VerificationUIState(
    var OTP:String = "",
    var isError: Boolean = false,
    var supportingText:String = "Enter OTP",
    var verificationStarted:Boolean = false
)
