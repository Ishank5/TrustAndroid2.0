package com.example.trustandroid20

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(){
    private val userRepository: com.example.trustandroid20.UserRepository

    init {
        userRepository = com.example.trustandroid20.UserRepository(
            FirebaseAuth.getInstance(),
            com.example.trustandroid20.Injection.instance()
        )
    }

    private val _authResult = MutableLiveData<com.example.trustandroid20.Result<Boolean>>()
    val authResult: LiveData<com.example.trustandroid20.Result<Boolean>> get() = _authResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }
}