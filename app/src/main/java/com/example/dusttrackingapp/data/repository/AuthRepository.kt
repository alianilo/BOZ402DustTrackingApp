package com.example.dusttrackingapp.data.repository

import com.example.dusttrackingapp.data.model.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<Unit>
    suspend fun register(email: String, password: String): AuthResult<Unit>
    suspend fun resetPassword(email: String): AuthResult<Unit>
    suspend fun register(name: String, email: String, password: String): AuthResult<Unit>
    fun logout()
    fun currentUser(): FirebaseUser?
}

