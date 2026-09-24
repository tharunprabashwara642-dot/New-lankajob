package com.example.domain.repository

import com.example.domain.model.NotificationPreference
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    val preferences: Flow<NotificationPreference>
    suspend fun updatePreferences(preferences: NotificationPreference)
}
