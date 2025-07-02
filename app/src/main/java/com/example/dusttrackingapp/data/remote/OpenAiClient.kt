package com.example.dusttrackingapp.data.remote

import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object OpenAiClient {

    private const val BASE_URL = "https://api.openai.com/"

    /**
     *  Build Retrofit with:
     *  • Authorization header (“Bearer …”)  – ALWAYS
     *  • 2-line Logcat output  (tag = "AI_HTTP")
     */
    fun create(apiKey: String): OpenAiApi {

        val headerInterceptor = Interceptor { chain ->
            val newReq = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(newReq)
        }

        val logInterceptor = HttpLoggingInterceptor { msg ->
            android.util.Log.d("AI_HTTP", msg)
        }.apply { level = HttpLoggingInterceptor.Level.BODY }

        val ok = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(logInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(ok)
            .addConverterFactory(
                MoshiConverterFactory.create(Moshi.Builder().build())
            )
            .build()
            .create(OpenAiApi::class.java)
    }
}
