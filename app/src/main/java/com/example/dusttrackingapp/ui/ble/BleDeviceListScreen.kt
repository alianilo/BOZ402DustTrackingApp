package com.example.dusttrackingapp.ui.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Bluetooth cihaz listesi – Material 3 tasarımlı
 *
 * @param onLogout    Çıkış yap butonuna basıldığında tetiklenir (LoginActivity’e dönmek için).
 * @param onConnected “Dust Sensor”a başarıyla bağlanınca tetiklenir (MainScreen’e geçmek için).
 */
@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BleDeviceListScreen(
    vm: BleViewModel = viewModel(),
    onLogout: () -> Unit = {},
    onConnected: (BluetoothGatt) -> Unit = {}
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val devices      by vm.devices.collectAsState()
    val isConnecting by vm.isConnecting.collectAsState()
    val isScanning   by vm.isScanning.collectAsState()

    /* ——— Adsız cihazları göster / gizle anahtarı ——— */
    var showUnnamed by remember { mutableStateOf(false) }
    val visibleDevices = remember(devices, showUnnamed) {
        devices.filter { showUnnamed || (it.device.name?.isNotBlank() == true) }
    }

    /* ——— Gerekli izinler ——— */
    val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }
    val permState: MultiplePermissionsState = rememberMultiplePermissionsState(perms)
    val enableBtLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {}

    LaunchedEffect(permState.allPermissionsGranted) {
        if (permState.allPermissionsGranted) {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
                Toast.makeText(ctx, "Lütfen Bluetooth'u açın", Toast.LENGTH_LONG).show()
                enableBtLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            } else vm.startScan()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bluetooth Cihazları") },
                actions = {
                    IconButton(onClick = {
                        vm.stopScan(); vm.startScan()
                        Toast.makeText(ctx, "Tarama yenilendi", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Yenile")
                    }
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Çıkış Yap")
                    }
                }
            )
        }
    ) { pad ->

        /* ——— İzin henüz yoksa ——— */
        if (!permState.allPermissionsGranted) {
            Column(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Bluetooth taraması için izin gerekli.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { permState.launchMultiplePermissionRequest() }) {
                    Text("İzin Ver")
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            /* —— Adsız cihaz anahtarı —— */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Adsız cihazları göster")
                Spacer(Modifier.weight(1f))
                Switch(checked = showUnnamed, onCheckedChange = { showUnnamed = it })
            }

            /* —— Tarama devam ediyor göstergesi —— */
            if (isScanning) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Tarama sürüyor…", style = MaterialTheme.typography.labelLarge)
                }
            }

            /* —— Liste / Boş görünüm —— */
            if (visibleDevices.isEmpty() && !isScanning) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cihaz bulunamadı")
                }
            } else {
                LazyColumn {
                    items(visibleDevices) { ble ->
                        val isDustSensor = (ble.device.name ?: "") == "Dust Sensor"

                        ElevatedCard(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .fillMaxWidth()
                                .background(
                                    if (isDustSensor) Color(0xFFA7F4B1) // Pastel yeşil
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable(enabled = isDustSensor) {
                                    scope.launch {
                                        vm.connect(
                                            ble.device,
                                            onConnected = { gatt -> onConnected(gatt) },
                                            onFailed = {
                                                Toast.makeText(ctx, it, Toast.LENGTH_LONG).show()
                                                vm.startScan()
                                            }
                                        )
                                    }
                                },
                            elevation = CardDefaults.elevatedCardElevation(
                                defaultElevation = if (isDustSensor) 4.dp else 1.dp
                            )
                        ) {
                            ListItem(
                                headlineContent   = { Text(ble.device.name ?: "Adsız") },
                                supportingContent = { Text(ble.device.address) }
                            )
                        }
                    }
                }
            }
        }
    }
}
