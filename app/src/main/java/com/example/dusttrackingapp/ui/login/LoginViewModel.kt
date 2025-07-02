package com.example.dusttrackingapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dusttrackingapp.data.model.AuthResult
import com.example.dusttrackingapp.data.repository.AuthRepository
import com.example.dusttrackingapp.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


class LoginViewModel(
    private val repo: AuthRepository = FirebaseAuthRepository()
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set


    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged    -> state = state.copy(email = event.value, error = null)
            is LoginEvent.PasswordChanged -> state = state.copy(password = event.value, error = null)
            LoginEvent.LoginClicked       -> authOperation { repo.login(state.email, state.password) }
            LoginEvent.RegisterClicked    -> authOperation { repo.register(state.email, state.password) }
            LoginEvent.ForgotPasswordClicked -> authOperation { repo.resetPassword(state.email) }
        }
    }

    private fun authOperation(block: suspend () -> AuthResult<Unit>) = viewModelScope.launch {
        state = state.copy(isLoading = true, error = null, info = null)
        when (val result = block()) {
            is AuthResult.Success -> state = state.copy(
                isLoading = false,
                info = "İşlem başarılı!",
                error = null
            )
            is AuthResult.Error   -> state = state.copy(
                isLoading = false,
                error = result.message,
                info = null
            )
            else -> Unit
        }
    }
}
