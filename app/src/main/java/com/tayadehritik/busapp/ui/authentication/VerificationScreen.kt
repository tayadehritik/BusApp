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
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.VersionField
import com.tayadehritik.busapp.ui.common.LoadingDialog



@Composable
fun VerificationScreen(
    verificationId:String?,
    onNavigateToHomeScreen:() -> Unit,
    viewModel: VerificationScreenViewModel
)
{
    val verificationUIState by viewModel.verificationUIState.collectAsState()

    Verfication(
        OTP = verificationUIState.OTP,
        isError = verificationUIState.isError,
        supportingText = verificationUIState.supportingText,
        verificationStarted = verificationUIState.verificationStarted,
        updateOTP = { viewModel.updateOTP(it)},
        verifyOTP = {
            viewModel.verifyOTP(verificationId,
            moveToHomeScreen = {
                onNavigateToHomeScreen()
            }
        )}
    )
}

@Composable
fun Verfication(
    OTP:String,
    isError:Boolean,
    supportingText:String,
    verificationStarted:Boolean,
    updateOTP:(String) -> Unit,
    verifyOTP:() -> Unit
)
{
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    )
    {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        )
        {
            Column (
                modifier = Modifier
                    .widthIn(200.dp,300.dp)
            )
            {
                Text(
                    text = "Verify your OTP",
                    style = MaterialTheme.typography.displaySmall
                )
                Spacer(modifier = Modifier
                    .width(10.dp)
                    .height(20.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = OTP ,
                    isError = isError,
                    onValueChange = updateOTP,
                    label = { Text(text = "OTP")},
                    supportingText = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = supportingText,
                            textAlign = TextAlign.Start,
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Limit: ${OTP.length}/6",
                            textAlign = TextAlign.End,
                        )
                    },
                    trailingIcon = {
                       if(OTP.isNotEmpty()){
                            IconButton(onClick = { updateOTP("") }) {
                                Icon(imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Clear OTP")
                            }
                       }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(
                        onDone = { verifyOTP() }
                    )
                )

                Spacer(modifier = Modifier
                    .width(10.dp)
                    .height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { verifyOTP() }
                ) {
                    Text(text = "Verify")
                }
            }

            if(verificationStarted)
            {
                LoadingDialog(text = "Verifying OTP")
            }
        }
    }

}