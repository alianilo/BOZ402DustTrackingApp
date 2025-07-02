package com.example.dusttrackingapp.data.repository

import com.example.dusttrackingapp.data.model.AuthResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.ktx.userProfileChangeRequest

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult<Unit> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Giriş hatası")
        }

    override suspend fun register(email: String, password: String): AuthResult<Unit> =
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Kayıt hatası")
        }

    override suspend fun resetPassword(email: String): AuthResult<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "E-posta gönderilemedi")
        }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): AuthResult<Unit> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()

        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        result.user?.updateProfile(profileUpdates)?.await()

        Firebase.firestore.collection("users")
            .document(result.user!!.uid)
            .set(mapOf("name" to name, "email" to email))
            .await()

        AuthResult.Success(Unit)

    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "Kayıt hatası")
    }


    override fun logout() = auth.signOut()

    override fun currentUser(): FirebaseUser? = auth.currentUser
}
