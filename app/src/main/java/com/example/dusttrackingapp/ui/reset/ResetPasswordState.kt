package com.example.dusttrackingapp.ui.reset

data class ResetPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val info: String? = null
)
