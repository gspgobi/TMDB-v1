package com.gobidev.tmdbv1.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gobidev.tmdbv1.domain.usecase.LoginUseCase
import com.gobidev.tmdbv1.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Username and password are required")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            _loginState.value = when (val result = loginUseCase(username, password)) {
                is Result.Success -> LoginState.Success
                is Result.Error -> LoginState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
