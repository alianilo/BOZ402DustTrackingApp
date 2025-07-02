package com.example.dusttrackingapp.ui.reset

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dusttrackingapp.data.model.AuthResult
import com.example.dusttrackingapp.data.repository.AuthRepository
import com.example.dusttrackingapp.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val repo: AuthRepository = FirebaseAuthRepository()
) : ViewModel() {

    var state by mutableStateOf(ResetPasswordState())
        private set

    fun onEvent(event: ResetPasswordEvent) {
        when (event) {
            is ResetPasswordEvent.EmailChanged -> state = state.copy(email = event.value, error = null)
            ResetPasswordEvent.Submit -> submit()
        }
    }

    private fun submit() = viewModelScope.launch {
        if (state.email.isBlank()) {
            state = state.copy(error = "E-posta zorunludur")
            return@launch
        }
        state = state.copy(isLoading = true, error = null, info = null)
        when (val res = repo.resetPassword(state.email)) {
            is AuthResult.Success -> state = state.copy(isLoading = false, info = "Sıfırlama e-postası gönderildi")
            is AuthResult.Error   -> state = state.copy(isLoading = false, error = res.message)
            else -> Unit
        }
    }
}
