package com.tayadehritik.busapp.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tayadehritik.busapp.ui.common.LoadingDialog



@Composable
fun LoginOrSignUpScreen(
    onNavigateToVerificationScreen:(verificationId:String) -> Unit,
    onNavigateToHomeScreen:() -> Unit,
    viewModel: LoginOrSignUpViewModel
){

    val loginOrSignUpUIState by viewModel.loginOrSignUpUIState.collectAsState()
    LoginOrSignUp(
        loginOrSignUpUIState.loginStarted,
        loginOrSignUpUIState.phoneNumber,
        loginOrSignUpUIState.isError,
        loginOrSignUpUIState.supportingText,
        loginOrSignUpUIState.loadingMessage,
        onInputPhoneNumber = {viewModel.inputPhoneNumber(it)},
        onSubmitPhoneNumber = {viewModel.submitPhoneNumber(
            moveToVerifyScreen = { verificationId ->
                onNavigateToVerificationScreen(verificationId)
            },
            moveToHomeScreen = {
                onNavigateToHomeScreen()
            }
        )}
    )

}

@Composable
fun LoginOrSignUp(
    loginStarted:Boolean,
    phoneNumber:String,
    isError:Boolean,
    supportingText:String,
    loadingMessage:String,
    onInputPhoneNumber: (phoneNumber:String) -> Unit,
    onSubmitPhoneNumber: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        )
        {

            Column(
                modifier = Modifier
                    .widthIn(200.dp,300.dp)
                ) {
                Text(
                    text = "Welcome to\nPune Bus App",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(modifier = Modifier
                    .width(10.dp)
                    .height(20.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = phoneNumber,
                    isError = isError,
                    leadingIcon = { Icon(Icons.Rounded.Call, contentDescription = "Phone Number") },
                    trailingIcon = {
                        if(phoneNumber.isNotEmpty()) {
                            IconButton(onClick = { onInputPhoneNumber("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Clear Phone Number"
                                )
                            }
                        }
                    },
                    onValueChange = onInputPhoneNumber,
                    prefix = { Text("+91") },
                    label = { Text("Phone Number") },
                    supportingText = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = supportingText,
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Limit: ${phoneNumber.length}/10",
                            textAlign = TextAlign.End,
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(
                        onDone = { onSubmitPhoneNumber() }
                    )
                )
                Spacer(modifier = Modifier
                    .width(10.dp)
                    .height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSubmitPhoneNumber() }
                ) {
                    Text(text = "Log In")
                }
            }

            if(loginStarted)
            {
                LoadingDialog(loadingMessage)
                keyboardController?.hide()

            }
        }

    }
}



