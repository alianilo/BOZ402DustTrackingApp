package com.example.dusttrackingapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.dusttrackingapp.ui.theme.DustTrackingTheme
import com.example.dusttrackingapp.ui.ble.BleDeviceListActivity

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { DustTrackingTheme {
            LoginScreen(
                onLoginSuccess = {
                    startActivity(Intent(this, BleDeviceListActivity::class.java))
                    finish()
                }
            )
        } }
    }

}
