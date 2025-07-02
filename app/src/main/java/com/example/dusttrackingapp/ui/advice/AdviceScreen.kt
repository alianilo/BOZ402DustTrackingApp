package com.example.dusttrackingapp.ui.advice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dusttrackingapp.data.remote.OpenAiApi
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdviceScreen(
    api: OpenAiApi,
    vm: AdviceViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val logs by vm.logs.collectAsState()
    val ai by vm.aiResponse.collectAsState()
    val df = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    /* ---- LazyColumn scroll state ---- */
    val listState = rememberLazyListState()

    /* ---- Yanıt geldiğinde alta kaydır ---- */
    LaunchedEffect(ai) {
        if (ai != null) {
            // Son öğe = ölçüm sayısı (0-based) + açıklama blokları
            listState.animateScrollToItem(index = logs.size + 2)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },
                title = { Text("Tavsiyeler (AI)") }
            )
        }
    ) { pad ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /* Açıklama metni */
            item {
                Text(
                    "Aşağıda bu saate ait en yüksek 20 sonuç görüntülenmektedir. " +
                            "Tavsiye Al butonuna basarsanız yapay zeka ölçüm sonuçlarınızı " +
                            "değerlendirip size en uygun önerileri sunacaktır.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            /* Ölçümler */
            items(logs) { l ->
                ListItem(
                    headlineContent = {
                        Text("%.1f µg/m³".format(l.value),
                            style = MaterialTheme.typography.bodyMedium)
                    },
                    supportingContent = { Text(df.format(l.timestamp.toDate())) }
                )
                Divider()
            }

            /* Tavsiye Al butonu */
            item {
                Button(
                    onClick = { vm.requestAdvice(api) },
                    enabled = logs.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Tavsiye Al") }
            }

            /* AI cevabı kartı */
            if (ai != null) {
                item {
                    Card(
                        colors = CardDefaults.elevatedCardColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Yapay Zeka Değerlendirmesi",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                        Divider()
                        Text(
                            ai!!,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
