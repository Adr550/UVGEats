package com.uvg.uvgeats.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.uvg.uvgeats.data.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun register(email: String, password: String): Result<FirebaseUser>
    suspend fun logout(): Result<Boolean>
    fun getCurrentUser(): FirebaseUser?
    fun isUserLoggedIn(): Flow<Boolean>
}

class AuthRepositoryImpl : AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val user = authResult.user
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val user = authResult.user
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(Exception("Error al crear usuario"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun logout(): Result<Boolean> {
        return try {
            firebaseAuth.signOut()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun isUserLoggedIn(): Flow<Boolean> = flow {
        emit(firebaseAuth.currentUser != null)
    }
}
