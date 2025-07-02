package com.example.dusttrackingapp.ui.logs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    vm: LogsViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val state by vm.state.collectAsState()
    val df = remember { SimpleDateFormat("dd MMM yyyy  HH:mm:ss", Locale.getDefault()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                title = { Text("Kayıtlar") },
                actions = { IconButton(onClick = { vm.onEvent(LogsEvent.Refresh) }) { Icon(Icons.Default.Refresh, "Yenile") } }
            )
        }
    ) { pad ->
        Column(modifier = Modifier.padding(pad).fillMaxSize()) {

            /* --- Filtre & Sıralama Butonları --- */
            FilterRow(state, vm::onEvent)
            SortRow(state, vm::onEvent)

            /* --- Kayıt listesi --- */
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(Modifier.weight(1f)) {
                    items(state.logs) { log ->
                        ListItem(
                            headlineContent = { Text("%.1f µg/m³".format(log.value)) },
                            supportingContent = { Text(df.format(log.timestamp.toDate())) }
                        )
                        Divider()
                    }
                }
            }

            /* --- Sayfa düğmeleri --- */
            if (state.logs.isNotEmpty()) {
                PaginationRow(state.page, state.hasNext) { page ->
                    vm.onEvent(LogsEvent.SelectPage(page))
                }
            }
        }
    }
}

/* ----------------- UI Helper Composables ----------------- */

@Composable
private fun FilterRow(state: LogsState, on: (LogsEvent) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(8.dp), Arrangement.spacedBy(8.dp)) {
        FilterChip(label = "1s",  selected = state.filter==LogsFilter.LAST_1H)  { on(LogsEvent.SelectFilter(LogsFilter.LAST_1H)) }
        FilterChip(label = "3s",  selected = state.filter==LogsFilter.LAST_3H)  { on(LogsEvent.SelectFilter(LogsFilter.LAST_3H)) }
        FilterChip(label = "Bugün", selected = state.filter==LogsFilter.TODAY) { on(LogsEvent.SelectFilter(LogsFilter.TODAY)) }
        FilterChip(label = "Tümü", selected = state.filter==LogsFilter.ALL)   { on(LogsEvent.SelectFilter(LogsFilter.ALL)) }
    }
}

@Composable
private fun SortRow(state: LogsState, on: (LogsEvent) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal=8.dp), Arrangement.spacedBy(8.dp)) {
        FilterChip("↓ Değer", state.sort==LogsSort.VALUE_DESC) { on(LogsEvent.SelectSort(LogsSort.VALUE_DESC)) }
        FilterChip("↑ Değer", state.sort==LogsSort.VALUE_ASC)  { on(LogsEvent.SelectSort(LogsSort.VALUE_ASC)) }
        FilterChip("En Yeni",   state.sort==LogsSort.DATE_DESC)  { on(LogsEvent.SelectSort(LogsSort.DATE_DESC)) }
    }
}

@Composable
private fun PaginationRow(page: Int, hasNext: Boolean, onPage:(Int)->Unit) {
    Row(
        Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        if (page > 1) TextButton(onClick = { onPage(page-1) }) { Text("‹") }
        Text("$page", modifier = Modifier.padding(horizontal = 16.dp))
        if (hasNext) TextButton(onClick = { onPage(page+1) }) { Text("›") }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    )
}
