package com.example.dusttrackingapp.ui.ble

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BleViewModel(app: Application) : AndroidViewModel(app) {

    /* —— UUID’ler (ESP32’de kullandığınızla aynı) —— */
    companion object {
        const val SERVICE_UUID   = "12345678-1234-1234-1234-1234567890AB"
        const val DUST_CHAR_UUID = "12345678-1234-1234-1234-1234567890DC"
    }

    /* —— Ekran durumu akışları —— */
    private val _devices      = MutableStateFlow<List<BleDevice>>(emptyList())
    private val _isScanning   = MutableStateFlow(false)
    private val _isConnecting = MutableStateFlow(false)
    private val _dust         = MutableStateFlow<Float?>(null)

    /** Dışarıya yalnızca salt-okunur StateFlow’lar veriyoruz */
    val devices:      StateFlow<List<BleDevice>> = _devices
    val isScanning:   StateFlow<Boolean>         = _isScanning
    val isConnecting: StateFlow<Boolean>         = _isConnecting
    val dust:         StateFlow<Float?>          = _dust

    /* —— BLE tarama —— */
    private var scanner: BluetoothLeScanner? = null
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(type: Int, res: ScanResult) {
            _devices.update { list ->
                if (list.any { it.device.address == res.device.address }) list
                else (list + BleDevice(res.device, res.rssi))
                    .sortedBy { it.device.name ?: "" }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        scanner = adapter.bluetoothLeScanner
        scanner?.startScan(scanCallback)
        _isScanning.value = true
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanner?.stopScan(scanCallback)
        _isScanning.value = false
    }

    /* —— Cihaza bağlan —— */
    @SuppressLint("MissingPermission")
    fun connect(
        target: BluetoothDevice,
        onConnected: (BluetoothGatt) -> Unit,
        onFailed:    (String) -> Unit
    ) {
        viewModelScope.launch {
            _isConnecting.value = true
            stopScan()

            target.connectGatt(getApplication(), false,
                object : BluetoothGattCallback() {

                    override fun onConnectionStateChange(
                        g: BluetoothGatt, status: Int, newState: Int
                    ) {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            g.discoverServices()
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            _isConnecting.value = false
                            onFailed("Bağlantı kesildi")
                        }
                    }

                    override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
                        val service = g.getService(UUID.fromString(SERVICE_UUID)) ?: return
                        val char    = service.getCharacteristic(
                            UUID.fromString(DUST_CHAR_UUID)
                        ) ?: return

                        /* Notify aç */
                        g.setCharacteristicNotification(char, true)
                        char.descriptors.firstOrNull()?.let { cccd ->
                            cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            g.writeDescriptor(cccd)
                        }

                        _isConnecting.value = false
                        onConnected(g)             // MainScreen’e gönder
                    }

                    override fun onCharacteristicChanged(
                        g: BluetoothGatt,
                        characteristic: BluetoothGattCharacteristic
                    ) {
                        if (characteristic.uuid == UUID.fromString(DUST_CHAR_UUID)) {
                            characteristic.getStringValue(0)
                                ?.toFloatOrNull()
                                ?.let { value ->
                                    _dust.value = value
                                    Log.i("BLE_NOTIFY", "µg/m³=$value")


                                        val uid = Firebase.auth.currentUser?.uid ?: return@let
                                        Firebase.firestore
                                            .collection("users").document(uid)
                                            .collection("dustLogs")
                                            .add(
                                                mapOf(
                                                    "value" to value,
                                                    "timestamp" to Timestamp.now()
                                                ))
                                            .addOnSuccessListener {
                                                Log.i("FIRE_SAVE", "OK id=${it.id}  v=$value")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("FIRE_SAVE", "FAIL", e)           // ①  hatayı *görmek* şart
                                            }

                                }
                        }


                    }
                },
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    BluetoothDevice.TRANSPORT_LE else BluetoothDevice.TRANSPORT_AUTO
            )
        }
    }
}
