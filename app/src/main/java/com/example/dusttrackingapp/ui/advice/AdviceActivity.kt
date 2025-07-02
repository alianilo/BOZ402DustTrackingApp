package com.example.dusttrackingapp.ui.advice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.dusttrackingapp.BuildConfig
import com.example.dusttrackingapp.data.remote.OpenAiClient
import com.example.dusttrackingapp.ui.theme.DustTrackingTheme

class AdviceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = OpenAiClient.create(BuildConfig.OPENAI_API_KEY)

        setContent {
            DustTrackingTheme {
                AdviceScreen(api = api, onBack = { finish() })
            }
        }
    }
}
