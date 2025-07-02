package com.example.dusttrackingapp.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

private const val PAGE_SIZE = 60

class LogsViewModel : ViewModel() {

    private val _state = MutableStateFlow(LogsState())
    val state: StateFlow<LogsState> = _state

    private var lastDocSnapshot: com.google.firebase.firestore.DocumentSnapshot? = null

    init { loadPage(1) }

    fun onEvent(e: LogsEvent) {
        when (e) {
            is LogsEvent.SelectFilter -> { _state.value = _state.value.copy(filter = e.filter, page = 1); loadPage(1) }
            is LogsEvent.SelectSort   -> { _state.value = _state.value.copy(sort = e.sort,   page = 1); loadPage(1) }
            is LogsEvent.SelectPage   -> { loadPage(e.page) }
            LogsEvent.Refresh         -> { loadPage(_state.value.page, useCache = false) }
        }
    }

    private fun loadPage(page: Int, useCache: Boolean = true) = viewModelScope.launch {
        val uid = Firebase.auth.currentUser?.uid ?: return@launch
        _state.value = _state.value.copy(isLoading = true, error = null)

        try {
            var q: Query = Firebase.firestore
                .collection("users").document(uid)
                .collection("dustLogs")

            // --- Filtre ---
            val now = Timestamp.now()
            val cal = Calendar.getInstance().apply { time = now.toDate() }
            when (_state.value.filter) {
                LogsFilter.LAST_1H -> q = q.whereGreaterThan(
                    "timestamp", Timestamp(Date(now.seconds * 1000 - 3600_000)))
                LogsFilter.LAST_3H -> q = q.whereGreaterThan(
                    "timestamp", Timestamp(Date(now.seconds * 1000 - 3 * 3600_000)))
                LogsFilter.TODAY -> {
                    cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE,0); cal.set(Calendar.SECOND,0); cal.set(Calendar.MILLISECOND,0)
                    q = q.whereGreaterThan("timestamp", Timestamp(cal.time))
                }
                else -> Unit
            }

            // --- Sıralama ---
            q = when (_state.value.sort) {
                LogsSort.VALUE_DESC -> q.orderBy("value", Query.Direction.DESCENDING)
                LogsSort.VALUE_ASC  -> q.orderBy("value", Query.Direction.ASCENDING)
                LogsSort.DATE_DESC  -> q.orderBy("timestamp", Query.Direction.DESCENDING)
            }

            // --- Sayfalama ---
            q = q.limit(PAGE_SIZE.toLong())
            if (page > 1 && useCache && lastDocSnapshot != null) {
                q = q.startAfter(lastDocSnapshot!!)
            }

            val snap = q.get().await()   // kotlinx-coroutines-play-services
            val logs = snap.documents.map {
                DustLog(
                    id = it.id,
                    value = (it.getDouble("value") ?: 0.0).toFloat(),
                    timestamp = it.getTimestamp("timestamp") ?: Timestamp.now()
                )
            }
            lastDocSnapshot = snap.documents.lastOrNull()
            _state.value = _state.value.copy(
                logs = logs,
                page = page,
                hasNext = logs.size == PAGE_SIZE,
                isLoading = false
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(isLoading = false, error = e.message)
        }
    }
}