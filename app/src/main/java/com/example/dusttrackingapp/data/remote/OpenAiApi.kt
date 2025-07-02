package com.example.dusttrackingapp.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiApi {

    @POST("v1/chat/completions")
    suspend fun chat(@Body req: ChatRequest): ChatResponse
}

/* --- DTO'lar (Moshi) --------------------------------------------------- */
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val max_tokens: Int = 300,
    val temperature: Double = 0.7
)

data class ChatMessage(val role: String, val content: String)

data class ChatResponse(val choices: List<Choice>) {
    data class Choice(val message: ChatMessage)
}
