package com.example.dusttrackingapp.ui.main

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.dusttrackingapp.ui.logs.LogsActivity
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userName: String,
    dustFlow: StateFlow<Float?>,
    onLogout: () -> Unit = {},
    onNavigateLogs: () -> Unit = {},
    onNavigateAi: () -> Unit = {}
) {
    val dust by dustFlow.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dust Tracking App") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Çıkış Yap")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(Modifier.height(24.dp))

            /* ——— Selamlama ——— */
            Text(
                text = "Merhaba $userName",
                style = MaterialTheme.typography.headlineSmall
            )

            /* ——— Canlı Gösterge ——— */
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.85f),
                elevation = CardDefaults.elevatedCardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Anlık Toz Yoğunluğu", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = dust?.let { "%.1f µg/m³".format(it) } ?: "—",
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            /* ——— Navigasyon Butonları ——— */
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Button(
                    onClick = onNavigateLogs,
                    modifier = Modifier.weight(1f)
                ) { Text("Kayıtlar") }

                OutlinedButton(
                    onClick = onNavigateAi,
                    modifier = Modifier.weight(1f)
                ) { Text("Tavsiyeler (AI)") }
            }
        }
    }
}
