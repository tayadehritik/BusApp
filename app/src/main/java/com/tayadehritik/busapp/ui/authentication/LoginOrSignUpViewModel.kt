package com.tayadehritik.busapp.ui.authentication
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.navigation.NavHostController

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.auth
import com.tayadehritik.busapp.data.LoginOrSignUpUIState
import com.tayadehritik.busapp.data.remote.PhoneAuthentication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginOrSignUpViewModel @Inject constructor(
    private val phoneAuthentication: PhoneAuthentication
): ViewModel() {

    private val _loginOrSignUpUIState = MutableStateFlow(LoginOrSignUpUIState())
    val loginOrSignUpUIState:StateFlow<LoginOrSignUpUIState>  = _loginOrSignUpUIState.asStateFlow()



    fun inputPhoneNumber(phoneNumber:String) {
        val isError = validatePhoneNumberInput(phoneNumber)
        if(!isError) _loginOrSignUpUIState.value = LoginOrSignUpUIState(
            phoneNumber,
            isError,
            loginStarted = loginOrSignUpUIState.value.loginStarted,
            loadingMessage = loginOrSignUpUIState.value.loadingMessage
        )
    }

    private fun validatePhoneNumberInput(text: String):Boolean {
        return text.length > 10 || Regex("""\D""").containsMatchIn(text)
    }

    private fun validatePhoneNumber(phoneNumber:String): Boolean {
        val pattern = Regex("""^\+91[\d]{10}$""")
        return pattern.matches(phoneNumber)
    }

    fun submitPhoneNumber(
        moveToVerifyScreen:(verificationId:String) -> Unit,
        moveToHomeScreen:() -> Unit
        ) {
        val phoneNumber = loginOrSignUpUIState.value.phoneNumber
        val isWrong = !validatePhoneNumber("+91$phoneNumber")
        if(isWrong){
            _loginOrSignUpUIState.value = LoginOrSignUpUIState(phoneNumber,true,"Invalid number")
        }
        else {
            _loginOrSignUpUIState.value = LoginOrSignUpUIState(phoneNumber, loginStarted = true, loadingMessage = "Sending OTP")

            phoneAuthentication.loginWithPhoneNumber("+91$phoneNumber",
                onCodeSentVM = { verificationId, token ->
                    _loginOrSignUpUIState.value = LoginOrSignUpUIState(phoneNumber, loginStarted = false)
                    println("Code is sent")
                    moveToVerifyScreen(verificationId)
                    //move to verify screen with verificationId
                },
                onVerificationFailedVM = {
                    _loginOrSignUpUIState.value = LoginOrSignUpUIState(phoneNumber,true, "Something went wrong",loginStarted = false)
                    println("Error occurred")
                },
                onVerificationCompletedVM = {credential: PhoneAuthCredential ->
                    println("verification completed")
                    _loginOrSignUpUIState.value = LoginOrSignUpUIState(phoneNumber, loginStarted = true, loadingMessage = "Verified - Signing in user")
                    phoneAuthentication.signInWithPhoneAuthCredential(
                        credential,
                        onFailure = {
                            _loginOrSignUpUIState.value = LoginOrSignUpUIState(phoneNumber,true, "Something went wrong",loginStarted = false)
                        },
                        onSuccess = {
                            //move to main view
                            _loginOrSignUpUIState.value = LoginOrSignUpUIState(phoneNumber, loginStarted = false)
                            moveToHomeScreen()

                        }
                    )

                }
            )
            //loginWithPhoneNumber(phoneNumber)
        }

    }



}