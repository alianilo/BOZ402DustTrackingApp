package com.example.dusttrackingapp.ui.reset

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.dusttrackingapp.ui.theme.DustTrackingTheme

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DustTrackingTheme {
                ResetPasswordScreen(onClose = { finish() })
            }
        }
    }
}
