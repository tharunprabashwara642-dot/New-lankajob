package com.example.domain.repository

import com.example.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    val isUserSignedIn: Flow<Boolean>
    suspend fun signInWithGoogleAccount(email: String, displayName: String): Result<UserProfile>
    suspend fun signInWithGoogle(): Result<UserProfile>
    suspend fun signOut(): Result<Unit>
    suspend fun completeProfile(
        fullName: String,
        phone: String,
        location: String,
        categories: List<String>,
        headline: String? = null
    ): Result<UserProfile>
    suspend fun updateProfile(profile: UserProfile): Result<Unit>
}
