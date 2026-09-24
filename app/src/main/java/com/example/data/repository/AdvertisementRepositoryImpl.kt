package com.example.data.repository

import com.example.core.AppPreferencesManager
import com.example.data.local.LankaJobsDatabase
import com.example.data.local.SeedData
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.AdStatus
import com.example.domain.model.Advertisement
import com.example.domain.model.AdvertisementPackage
import com.example.domain.repository.AdvertisementRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class AdvertisementRepositoryImpl(
    private val database: LankaJobsDatabase,
    private val appPreferencesManager: AppPreferencesManager? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AdvertisementRepository {

    private val adDao = database.advertisementDao()

    override fun getApprovedAdvertisements(): Flow<List<Advertisement>> {
        return adDao.getApprovedAdvertisements().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun getAllAdvertisements(): Flow<List<Advertisement>> {
        return adDao.getAllAdvertisements().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun getSuggestedAdvertisements(
        preferredCategories: List<String>,
        preferredLocation: String
    ): Flow<List<Advertisement>> {
        return adDao.getApprovedAdvertisements().map { entities ->
            val allAds = entities.map { it.toDomain() }
            if (preferredCategories.isEmpty() && preferredLocation.isBlank()) {
                allAds
            } else {
                val normalizedLoc = preferredLocation.trim().lowercase()
                val normalizedCats = preferredCategories.map { it.trim().lowercase() }

                allAds.map { ad ->
                    var score = 0
                    val adCat = ad.category.lowercase()
                    val adLoc = ad.location.lowercase()

                    if (normalizedCats.any { adCat.contains(it) || it.contains(adCat) }) {
                        score += 3
                    }
                    if (normalizedLoc.isNotBlank() && (adLoc.contains(normalizedLoc) || normalizedLoc.contains(adLoc))) {
                        score += 2
                    }
                    if (adLoc.contains("remote") || adLoc.contains("island")) {
                        score += 1
                    }
                    Pair(ad, score)
                }
                    .filter { it.second > 0 }
                    .sortedWith(compareByDescending<Pair<Advertisement, Int>> { it.second }.thenByDescending { it.first.createdAt })
                    .map { it.first }
                    .ifEmpty {
                        allAds.take(3)
                    }
            }
        }.flowOn(ioDispatcher)
    }

    override fun getAdvertisementById(id: String): Flow<Advertisement?> {
        return adDao.getAdvertisementById(id).map { entity ->
            entity?.toDomain()
        }.flowOn(ioDispatcher)
    }

    override fun getPackages(): List<AdvertisementPackage> {
        val base = SeedData.packages
        if (appPreferencesManager == null) return base
        return base.map { pkg ->
            val currentPrice = appPreferencesManager.getPackagePrice(pkg.id, pkg.priceLkr)
            val currentDuration = appPreferencesManager.getPackageDuration(pkg.id, pkg.durationDays)
            pkg.copy(priceLkr = currentPrice, durationDays = currentDuration)
        }
    }

    override fun getUserAdvertisements(userId: String): Flow<List<Advertisement>> {
        return adDao.getUserAdvertisements(userId).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override suspend fun submitAdvertisement(ad: Advertisement): Result<String> = withContext(ioDispatcher) {
        try {
            val finalId = if (ad.id.isBlank()) "ad_usr_${UUID.randomUUID().toString().take(8)}" else ad.id
            // Verify price from package source of truth
            val pkg = getPackages().find { it.id == ad.packageId }
            val authoritativePrice = pkg?.priceLkr ?: ad.amountPaidLkr

            val submission = ad.copy(
                id = finalId,
                amountPaidLkr = authoritativePrice,
                packageName = pkg?.name ?: ad.packageName,
                status = AdStatus.PAYMENT_SUBMITTED,
                createdAt = System.currentTimeMillis()
            )
            adDao.insertOrUpdate(submission.toEntity())
            Result.success(finalId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAdvertisementStatus(
        adId: String,
        status: AdStatus,
        reason: String?
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            adDao.updateStatusWithReason(adId, status.name, reason)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAdvertisement(adId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            adDao.deleteAdvertisement(adId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePackagePrice(packageId: String, newPriceLkr: Int): Result<Unit> = withContext(ioDispatcher) {
        try {
            appPreferencesManager?.setPackagePrice(packageId, newPriceLkr)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePackageDuration(packageId: String, durationDays: Int): Result<Unit> = withContext(ioDispatcher) {
        try {
            appPreferencesManager?.setPackageDuration(packageId, durationDays)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
