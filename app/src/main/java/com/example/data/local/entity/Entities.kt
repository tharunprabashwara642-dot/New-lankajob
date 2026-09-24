package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "jobs_cache",
    indices = [
        Index(value = ["isFeatured"]),
        Index(value = ["categoryId"]),
        Index(value = ["cachedAt"])
    ]
)
data class JobEntity(
    @PrimaryKey val id: String,
    val title: String,
    val companyId: String,
    val companyName: String,
    val companyLogoUrl: String?,
    val categoryId: String,
    val categoryName: String,
    val location: String,
    val district: String,
    val employmentType: String,
    val experienceLevel: String,
    val workplaceType: String,
    val salaryMin: Int?,
    val salaryMax: Int?,
    val salaryCurrency: String,
    val isSalaryNegotiable: Boolean,
    val description: String,
    val responsibilitiesJoined: String,
    val requirementsJoined: String,
    val benefitsJoined: String,
    val postedDate: String,
    val closingDate: String?,
    val isFeatured: Boolean,
    val isUrgent: Boolean,
    val applicationUrl: String?,
    val applicationEmail: String?,
    val source: String,
    val viewsCount: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_jobs")
data class SavedJobEntity(
    @PrimaryKey val jobId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val preferredLocation: String,
    val preferredCategoriesJoined: String,
    val headline: String?,
    val isProfileComplete: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "advertisements",
    indices = [
        Index(value = ["status"]),
        Index(value = ["createdAt"])
    ]
)
data class AdvertisementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val organizationName: String,
    val imageUrl: String?,
    val websiteUrl: String?,
    val contactPhone: String?,
    val contactEmail: String?,
    val location: String,
    val category: String,
    val packageId: String,
    val packageName: String,
    val status: String,
    val paymentMethod: String? = null,
    val paymentReference: String? = null,
    val amountPaidLkr: Int = 0,
    val rejectionReason: String? = null,
    val createdAt: Long,
    val validUntil: Long?,
    val userId: String?
)

@Entity(
    tableName = "payments",
    indices = [
        Index(value = ["status"]),
        Index(value = ["advertisementId"]),
        Index(value = ["userId"])
    ]
)
data class PaymentEntity(
    @PrimaryKey val id: String,
    val advertisementId: String,
    val userId: String,
    val packageId: String,
    val packageName: String,
    val amountLkr: Int,
    val paymentMethod: String,
    val paymentReference: String,
    val paymentDate: String,
    val receiptUrl: String?,
    val status: String,
    val rejectionReason: String? = null,
    val submittedAt: Long,
    val verifiedAt: Long? = null
)
