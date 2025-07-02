package com.example.dusttrackingapp.ui.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

object BleDataRepository {

    private const val DUST_CHAR_UUID = "12345678-1234-1234-1234-1234567890DC"

    @SuppressLint("MissingPermission")
    fun dustFlow(gatt: BluetoothGatt) = callbackFlow<Float> {
        // 1) Karakteristik bul
        val service = gatt.getService(UUID.fromString(BleViewModel.SERVICE_UUID))
        val char    = service?.getCharacteristic(UUID.fromString(DUST_CHAR_UUID))
        if (char == null) {
            close(IllegalStateException("Dust characteristic not found"))
            return@callbackFlow
        }

        // 2) Notify aç
        gatt.setCharacteristicNotification(char, true)
        char.descriptors.firstOrNull()?.let {
            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(it)
        }

        // 3) Var olan BluetoothGattCallback uygulaması (connect sırasında atanmıştı)
        //    her değer değiştiğinde Flow’a iletir:
        val listener = object : BluetoothGattCallback() {
            override fun onCharacteristicChanged(
                g: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                if (characteristic.uuid == UUID.fromString(DUST_CHAR_UUID)) {
                    characteristic.getStringValue(0)
                        ?.toFloatOrNull()
                        ?.let { trySend(it) }
                }
            }
        }
        // ‼️  Bağlantıyı tekrar kurmuyor, sadece “listener” ekliyoruz
        gatt.javaClass.getMethod(
            "registerAppCallback", BluetoothGattCallback::class.java
        ).invoke(gatt, listener)

        awaitClose {
            gatt.setCharacteristicNotification(char, false)
        }
    }
}
