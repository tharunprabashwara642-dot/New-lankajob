package com.example.data.repository

import com.example.domain.model.NotificationPreference
import com.example.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationRepositoryImpl : NotificationRepository {
    private val _preferences = MutableStateFlow(NotificationPreference())
    override val preferences: Flow<NotificationPreference> = _preferences.asStateFlow()

    override suspend fun updatePreferences(preferences: NotificationPreference) {
        _preferences.value = preferences
    }
}
