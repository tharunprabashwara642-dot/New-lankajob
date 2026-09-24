package com.example.domain.model

data class UserProfile(
    val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String = "",
    val preferredLocation: String = "",
    val preferredCategories: List<String> = emptyList(),
    val headline: String? = null,
    val isProfileComplete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class NotificationPreference(
    val newJobs: Boolean = true,
    val featuredJobs: Boolean = true,
    val advertisements: Boolean = true,
    val savedJobUpdates: Boolean = true
)
