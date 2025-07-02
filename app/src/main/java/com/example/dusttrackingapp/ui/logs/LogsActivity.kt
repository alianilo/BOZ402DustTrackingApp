package com.example.dusttrackingapp.ui.logs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.dusttrackingapp.ui.theme.DustTrackingTheme

class LogsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DustTrackingTheme { LogsScreen(onBack = { finish() }) } }
    }
}
