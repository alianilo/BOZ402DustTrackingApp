package com.example.dusttrackingapp.ui.register


sealed class RegisterEvent {
    data class NameChanged(val value: String)  : RegisterEvent()
    data class EmailChanged(val value: String) : RegisterEvent()
    data class PasswordChanged(val value: String) : RegisterEvent()
    object Submit : RegisterEvent()
}
