package com.example.dusttrackingapp.ui.logs

import com.google.firebase.Timestamp

data class DustLog(
    val id: String,
    val value: Float,
    val timestamp: Timestamp
)