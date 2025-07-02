package com.example.dusttrackingapp.ui.advice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dusttrackingapp.data.remote.*
import com.example.dusttrackingapp.ui.logs.DustLog
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

class AdviceViewModel : ViewModel() {

    private val _logs = MutableStateFlow<List<DustLog>>(emptyList())
    val logs: StateFlow<List<DustLog>> = _logs

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse

    private val db = Firebase.firestore
    private val uid = Firebase.auth.currentUser?.uid ?: ""

    init { fetchTopLogs() }


    fun fetchTopLogs() = viewModelScope.launch {
        val oneHourAgo = Timestamp(Date(Timestamp.now().seconds * 1000 - 3_600_000))
        val snap = db.collection("users").document(uid)
            .collection("dustLogs")
            .whereGreaterThan("timestamp", oneHourAgo)
            .orderBy("timestamp")
            .orderBy("value", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()


        _logs.value = snap.documents.map {
            DustLog(
                id = it.id,
                value = (it.getDouble("value") ?: 0.0).toFloat(),
                timestamp = it.getTimestamp("timestamp")!!
            )
        }
    }

    /** OpenAI GPT-4o'dan tavsiye alır */
    fun requestAdvice(api: OpenAiApi) = viewModelScope.launch {
        if (_logs.value.isEmpty()) return@launch

        val prompt = buildPrompt(_logs.value)
        try {
            val resp = api.chat(
                ChatRequest(
                    model = "gpt-4o-mini",
                    messages = listOf(
                        ChatMessage("system", "You are an air-quality expert."),
                        ChatMessage("user", prompt)
                    )
                )
            )
            _aiResponse.value = resp.choices.first().message.content
        } catch (e: Exception) {
            _aiResponse.value = "AI isteği başarısız: ${e.message}"
        }
    }

    private fun buildPrompt(list: List<DustLog>): String = buildString {
        appendLine("Aşağıda kapalı bir ortamda yapılan son 1 saate ait partikül ölçümleri (µg/m³) verilmiştir.")
        appendLine("Sensör 0–560 µg/m³ aralığında ölçüm yapmaktadır.")
        appendLine("Lütfen tüm cevabını TÜRKÇE yaz ve 150 kelimeyi geçmesin.\n")
        list.forEach { log ->
            appendLine("${log.timestamp.toDate()}  —  ${"%.1f".format(log.value)}")
        }
        appendLine(
            "\nBu verileri değerlendirerek havalandırma, temizlik yapma veya diğer alınabilecek önlemler konusunda kısa ve net tavsiyeler ver."
        )
    }
}
