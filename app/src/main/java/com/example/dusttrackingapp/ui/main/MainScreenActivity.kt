package com.example.dusttrackingapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.dusttrackingapp.ui.ble.BleGattHolder
import com.example.dusttrackingapp.ui.ble.BleVmHolder
import com.example.dusttrackingapp.ui.theme.DustTrackingTheme
import com.example.dusttrackingapp.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainScreenActivity : ComponentActivity() {

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bleVm = BleVmHolder.vm ?: return finish()
        val gatt  = BleGattHolder.gatt ?: return finish()


        var userName = "Kullanıcı"


        val uid = FirebaseAuth.getInstance().currentUser?.uid
        lifecycleScope.launch {
            val authName = FirebaseAuth.getInstance().currentUser?.displayName
            userName = authName ?: run {
                uid?.let {
                    FirebaseFirestore.getInstance()
                        .collection("users").document(it)
                        .get().await()
                        .getString("name")
                } ?: "Kullanıcı"
            }


            setContent {
                DustTrackingTheme {
                    MainScreen(
                        userName   = userName,
                        dustFlow   = bleVm.dust,
                        onLogout   = {
                            gatt.close()
                            BleVmHolder.vm = null
                            BleGattHolder.gatt = null
                            FirebaseAuth.getInstance().signOut()
                            startActivity(Intent(this@MainScreenActivity, LoginActivity::class.java))
                            finish()
                        },
                        onNavigateLogs = {
                            startActivity(Intent(this@MainScreenActivity, com.example.dusttrackingapp.ui.logs.LogsActivity::class.java))
                        },
                        onNavigateAi = { startActivity(Intent(this@MainScreenActivity, com.example.dusttrackingapp.ui.advice.AdviceActivity::class.java)) }
                    )
                }
            }
        }
    }
}
