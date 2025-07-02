package com.example.dusttrackingapp.ui.reset

sealed class ResetPasswordEvent {
    data class EmailChanged(val value: String) : ResetPasswordEvent()
    object Submit : ResetPasswordEvent()
}
