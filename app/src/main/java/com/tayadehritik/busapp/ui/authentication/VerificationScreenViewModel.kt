package com.tayadehritik.busapp.ui.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthProvider
import com.tayadehritik.busapp.data.VerificationUIState
import com.tayadehritik.busapp.data.remote.PhoneAuthentication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VerificationScreenViewModel(
): ViewModel() {

    private val _verificationUIState = MutableStateFlow(VerificationUIState())
    public var verificationUIState = _verificationUIState.asStateFlow()
    private val phoneAuthentication = PhoneAuthentication()


    fun updateOTP(OTP:String)
    {
        val isError = validateOTPInput(OTP)
        if(!isError) _verificationUIState.value  = VerificationUIState(
            OTP,
            verificationStarted = verificationUIState.value.verificationStarted
        )

    }

    private fun validateOTPInput(OTP:String):Boolean
    {
        return OTP.length > 6 || Regex("""\D""").containsMatchIn(OTP)
    }

    private fun validateOTP(OTP:String):Boolean
    {
        val pattern = Regex("""^[\d]{6}$""")
        return pattern.matches(OTP)
    }

    fun verifyOTP(
        verficationId:String?,
        moveToHomeScreen:() -> Unit
    )
    {
        val OTP = verificationUIState.value.OTP
        val isWrong = !validateOTP(OTP)

        if(isWrong)
        {
            _verificationUIState.value = VerificationUIState(
                OTP = OTP,
                isError = true,
                supportingText = "Invalid OTP Format",
                verificationStarted = false
            )
        }
        else
        {
            _verificationUIState.value = VerificationUIState(
                OTP = OTP,
                isError = false,
                verificationStarted = true,
            )

            val credential = PhoneAuthProvider.getCredential(verficationId!!, OTP)
            phoneAuthentication.signInWithPhoneAuthCredential(credential,
                onSuccess = {
                    _verificationUIState.value = VerificationUIState(
                        OTP = OTP,
                        isError = false,
                        verificationStarted = false
                    )
                    moveToHomeScreen()
                },
                onFailure = {errorMessage ->
                    _verificationUIState.value = VerificationUIState(
                        OTP = OTP,
                        isError = true,
                        supportingText = errorMessage,
                        verificationStarted = false
                    )
                }
            )

        }
    }
}