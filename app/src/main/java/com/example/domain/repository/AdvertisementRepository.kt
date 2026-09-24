package com.example.domain.repository

import com.example.domain.model.AdStatus
import com.example.domain.model.Advertisement
import com.example.domain.model.AdvertisementPackage
import kotlinx.coroutines.flow.Flow

interface AdvertisementRepository {
    fun getApprovedAdvertisements(): Flow<List<Advertisement>>
    fun getAllAdvertisements(): Flow<List<Advertisement>>
    fun getSuggestedAdvertisements(
        preferredCategories: List<String>,
        preferredLocation: String
    ): Flow<List<Advertisement>>
    fun getAdvertisementById(id: String): Flow<Advertisement?>
    fun getPackages(): List<AdvertisementPackage>
    fun getUserAdvertisements(userId: String): Flow<List<Advertisement>>
    suspend fun submitAdvertisement(ad: Advertisement): Result<String>
    suspend fun updateAdvertisementStatus(adId: String, status: AdStatus, reason: String? = null): Result<Unit>
    suspend fun deleteAdvertisement(adId: String): Result<Unit>
    suspend fun updatePackagePrice(packageId: String, newPriceLkr: Int): Result<Unit>
    suspend fun updatePackageDuration(packageId: String, durationDays: Int): Result<Unit>
}
