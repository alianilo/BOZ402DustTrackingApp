package com.example.dusttrackingapp.ui.main

import android.bluetooth.BluetoothGatt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dusttrackingapp.ui.ble.BleDataRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainScreenViewModel(
    gatt: BluetoothGatt            // Bağlantıyı Activity’den alıyoruz
) : ViewModel() {

    private val _dust = MutableStateFlow<Float?>(null)
    val dust: StateFlow<Float?> = _dust

    init {
        viewModelScope.launch {
            BleDataRepository.dustFlow(gatt)
                .onEach { saveToFirestore(it) }
                .catch { /* TODO: hata bildir */ }
                .collect { _dust.value = it }
        }
    }

    private fun saveToFirestore(value: Float) {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val doc = Firebase.firestore
            .collection("users")
            .document(uid)
            .collection("dustLogs")
            .document()

        doc.set(
            mapOf(
                "value" to value,
                "timestamp" to Timestamp.now()
            )
        )
    }
}
