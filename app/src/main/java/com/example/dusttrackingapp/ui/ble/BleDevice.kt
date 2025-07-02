package com.example.dusttrackingapp.ui.ble

import android.bluetooth.BluetoothDevice

data class BleDevice(val device: BluetoothDevice, val rssi: Int)
