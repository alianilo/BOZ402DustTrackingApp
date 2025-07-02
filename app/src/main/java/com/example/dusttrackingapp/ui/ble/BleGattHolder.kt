package com.example.dusttrackingapp.ui.ble

import android.bluetooth.BluetoothGatt

/** BluetoothGatt Parcelable olmadığından Activity’ler arasında bu şekilde tutuyoruz. */
object BleGattHolder { var gatt: BluetoothGatt? = null }
