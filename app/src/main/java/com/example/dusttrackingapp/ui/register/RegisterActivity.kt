package com.example.dusttrackingapp.ui.register

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.dusttrackingapp.BuildConfig
import com.example.dusttrackingapp.ui.theme.DustTrackingTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("AI_KEY_CHECK", BuildConfig.OPENAI_API_KEY)
        setContent {
            DustTrackingTheme {
                RegisterScreen(
                    onSuccess = { finish() },
                    onBack    = { finish() }
                )
            }
        }
    }
}
