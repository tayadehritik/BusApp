package com.tayadehritik.busapp.data.remote

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class PhoneAuthentication {

    var auth = FirebaseAuth.getInstance()

    fun loginWithPhoneNumber(
        phoneNumber: String,
        onCodeSentVM: (verificationId: String, token: PhoneAuthProvider.ForceResendingToken) -> Unit,
        onVerificationFailedVM: () -> Unit,
        onVerificationCompletedVM: (credential: PhoneAuthCredential) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                Log.d("TAG", "onVerificationCompleted:$credential")
                onVerificationCompletedVM(credential)
                //signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }
                onVerificationFailedVM()
                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:$verificationId")
                onCodeSentVM(verificationId, token)
                // Save verification ID and resending token so we can use them later
                //storedVerificationId = verificationId
                //resendToken = token
            }
        }

        //auth.firebaseAuthSettings.forceRecaptchaFlowForTesting(true)
        //auth.firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+916239091234","123456")

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    /*fun verifyOTP(credential: PhoneAuthCredential) {
        signInWithPhoneAuthCredential(credential)
    }

    fun verifyOTP(verificationId: String, OTP: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId,OTP)
        signInWithPhoneAuthCredential(credential)
    }*/

    public fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        onSuccess:() -> Unit,
        onFailure:(errorMessage:String) -> Unit
        ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    //move to main view
                    val user = task.result?.user

                    onSuccess()
                } else {
                    var errorMessage = "Something went wrong"
                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        errorMessage = "Incorrect OTP"
                    }
                    onFailure(errorMessage)
                    // Update UI
                    // Something Went wrong
                }
            }
    }
}