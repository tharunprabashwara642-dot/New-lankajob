package com.example.data.repository

import com.example.data.local.LankaJobsDatabase
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.UserProfile
import com.example.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val database: LankaJobsDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    private val userProfileDao = database.userProfileDao()

    override val currentUser: Flow<UserProfile?> = userProfileDao.getCurrentProfile()
        .map { it?.toDomain() }
        .flowOn(ioDispatcher)

    override val isUserSignedIn: Flow<Boolean> = userProfileDao.getCurrentProfile()
        .map { it != null }
        .flowOn(ioDispatcher)

    override suspend fun signInWithGoogleAccount(email: String, displayName: String): Result<UserProfile> = withContext(ioDispatcher) {
        try {
            val formattedName = if (displayName.isNotBlank()) {
                displayName.trim()
            } else {
                email.substringBefore("@")
                    .split(".", "_", "-")
                    .joinToString(" ") { part -> part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
            }

            val initialProfile = UserProfile(
                id = "usr_g_${Math.abs(email.hashCode())}",
                email = email.trim(),
                fullName = formattedName,
                phoneNumber = "",
                preferredLocation = "Colombo",
                preferredCategories = emptyList(),
                isProfileComplete = false
            )
            userProfileDao.insertOrUpdateProfile(initialProfile.toEntity())
            Result.success(initialProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(): Result<UserProfile> = withContext(ioDispatcher) {
        val current = currentUser.firstOrNull()
        if (current != null) {
            Result.success(current)
        } else {
            Result.failure(IllegalStateException("No Google account selected yet"))
        }
    }

    override suspend fun completeProfile(
        fullName: String,
        phone: String,
        location: String,
        categories: List<String>,
        headline: String?
    ): Result<UserProfile> = withContext(ioDispatcher) {
        try {
            val current = currentUser.firstOrNull()
            val email = current?.email ?: ""
            val id = current?.id ?: "usr_g_${Math.abs(email.hashCode())}"

            val updated = UserProfile(
                id = id,
                email = email,
                fullName = fullName.trim(),
                phoneNumber = phone.trim(),
                preferredLocation = location.trim(),
                preferredCategories = categories,
                headline = headline?.trim()?.ifBlank { null },
                isProfileComplete = true,
                createdAt = System.currentTimeMillis()
            )
            userProfileDao.insertOrUpdateProfile(updated.toEntity())
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(profile: UserProfile): Result<Unit> = withContext(ioDispatcher) {
        try {
            userProfileDao.insertOrUpdateProfile(profile.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> = withContext(ioDispatcher) {
        try {
            userProfileDao.clearProfile()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
