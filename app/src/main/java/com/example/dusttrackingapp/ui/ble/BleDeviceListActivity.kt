package com.example.dusttrackingapp.ui.ble

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.dusttrackingapp.ui.ble.BleVmHolder.vm
import com.example.dusttrackingapp.ui.login.LoginActivity
import com.example.dusttrackingapp.ui.main.MainScreenActivity
import com.example.dusttrackingapp.ui.theme.DustTrackingTheme

class BleDeviceListActivity : ComponentActivity() {
    private lateinit var vm: BleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[BleViewModel::class.java]
        setContent {
            DustTrackingTheme {
                BleDeviceListScreen(
                    onLogout  = {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    },
                    onConnected = {gatt ->
                        BleGattHolder.gatt = gatt
                        BleVmHolder.vm = vm
                        startActivity(Intent(this, MainScreenActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}