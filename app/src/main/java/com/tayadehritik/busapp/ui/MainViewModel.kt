package com.tayadehritik.busapp.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {

    private var auth: FirebaseAuth
    private val _startDestination = MutableStateFlow("")
    val startDestination = _startDestination.asStateFlow()
    init {
        auth = Firebase.auth
        if(auth.currentUser!=null)
        {
            _startDestination.value = "HomeScreen"
        }
        else
        {
            _startDestination.value = "LogInOrSignUpScreen"
        }
    }
}