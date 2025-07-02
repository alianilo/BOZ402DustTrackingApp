package com.example.dusttrackingapp.ui.register

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dusttrackingapp.data.model.AuthResult
import com.example.dusttrackingapp.data.repository.AuthRepository
import com.example.dusttrackingapp.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


class RegisterViewModel(
    private val repo: AuthRepository = FirebaseAuthRepository()
) : ViewModel() {

    // ❶ Compose’a gözlemlenebilir hâle getir
    var state by mutableStateOf(RegisterState())
        private set

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NameChanged     -> state = state.copy(name = event.value,  error = null)
            is RegisterEvent.EmailChanged    -> state = state.copy(email = event.value, error = null)
            is RegisterEvent.PasswordChanged -> state = state.copy(password = event.value, error = null)
            RegisterEvent.Submit             -> submit()
        }
    }

    private fun submit() = viewModelScope.launch {
        if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            state = state.copy(error = "İsim, e-posta ve şifre zorunludur!")
            return@launch
        }

        state = state.copy(isLoading = true, error = null, info = null)
        when (val result = repo.register(state.name, state.email, state.password)) {
            is AuthResult.Success -> state = state.copy(isLoading = false, info = "Kayıt tamamlandı!")
            is AuthResult.Error   -> state = state.copy(isLoading = false, error = result.message)
            else -> Unit
        }
    }
}
