package com.tayadehritik.busapp.ui.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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

@Composable
fun LoginOrSignUpScreen(
    viewModel:LoginOrSignUpViewModel
){
    val loginOrSignUpUIState by viewModel.loginOrSignUpUIState.collectAsState()
    LoginOrSignUp(
        loginOrSignUpUIState.phoneNumber,
        loginOrSignUpUIState.isError,
        onInputPhoneNumber = {viewModel.inputPhoneNumber(it)},
        onSubmitPhoneNumber = {viewModel.submitPhoneNumber(it)}
    )

}
@Composable
fun LoginOrSignUp(
    phoneNumber:String,
    isError:Boolean,
    onInputPhoneNumber: (phoneNumber:String) -> Unit,
    onSubmitPhoneNumber: (SoftwareKeyboardController?) -> Unit
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
                    onValueChange = onInputPhoneNumber,
                    prefix = { Text("+91") },
                    label = { Text("Phone Number") },
                    supportingText = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Log in or sign up",
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
                        onDone = { onSubmitPhoneNumber(keyboardController) }
                    )
                )
                Spacer(modifier = Modifier
                    .width(10.dp)
                    .height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSubmitPhoneNumber(keyboardController) }
                ) {
                    Text(text = "Log In")
                }

            }

        }
    }
}


