package com.tayadehritik.busapp.ui.authentication
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.navigation.NavHostController

import androidx.lifecycle.ViewModel
import com.tayadehritik.busapp.data.LoginOrSignUpUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginOrSignUpViewModel(
    navController: NavHostController
): ViewModel() {

    private val _loginOrSignUpUIState = MutableStateFlow(LoginOrSignUpUIState())
    val loginOrSignUpUIState:StateFlow<LoginOrSignUpUIState>  = _loginOrSignUpUIState.asStateFlow()


    fun inputPhoneNumber(phoneNumber:String) {
        val isError = validatePhoneNumberInput(phoneNumber)
        if(!isError) _loginOrSignUpUIState.value = LoginOrSignUpUIState(phoneNumber,isError)
    }

    private fun validatePhoneNumberInput(text: String):Boolean {
        return text.length > 10 || Regex("""\D""").containsMatchIn(text)
    }

    private fun validatePhoneNumber(phoneNumber:String): Boolean {
        val pattern = Regex("""^\+91[\d]{10}$""")
        return pattern.matches(phoneNumber)
    }

    fun submitPhoneNumber(keyboardController: SoftwareKeyboardController?) {
        val phoneNumber = loginOrSignUpUIState.value.phoneNumber
        val isWrong = !validatePhoneNumber("+91$phoneNumber")
        if(isWrong) _loginOrSignUpUIState.value = LoginOrSignUpUIState(phoneNumber,true) else keyboardController?.hide()
    }
}