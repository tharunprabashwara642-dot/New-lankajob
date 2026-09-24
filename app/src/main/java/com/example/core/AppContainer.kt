package com.example.core

import android.content.Context
import com.example.data.local.LankaJobsDatabase
import com.example.data.local.SeedData
import com.example.data.mapper.toEntity
import com.example.data.payment.ManualPaymentService
import com.example.data.payment.PaymentService
import com.example.data.repository.AdvertisementRepositoryImpl
import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.JobRepositoryImpl
import com.example.data.repository.NotificationRepositoryImpl
import com.example.data.repository.PaymentRepositoryImpl
import com.example.data.repository.SavedJobRepositoryImpl
import com.example.domain.repository.AdvertisementRepository
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.JobRepository
import com.example.domain.repository.NotificationRepository
import com.example.domain.repository.PaymentRepository
import com.example.domain.repository.SavedJobRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AppContainer {
    val jobRepository: JobRepository
    val savedJobRepository: SavedJobRepository
    val advertisementRepository: AdvertisementRepository
    val paymentRepository: PaymentRepository
    val authRepository: AuthRepository
    val notificationRepository: NotificationRepository
    val appPreferencesManager: AppPreferencesManager
    suspend fun preseedDatabaseIfNeeded()
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database: LankaJobsDatabase by lazy {
        LankaJobsDatabase.getInstance(context)
    }

    override val appPreferencesManager: AppPreferencesManager by lazy {
        AppPreferencesManager(context)
    }

    override val jobRepository: JobRepository by lazy {
        JobRepositoryImpl(database)
    }

    override val savedJobRepository: SavedJobRepository by lazy {
        SavedJobRepositoryImpl(database, jobRepository)
    }

    override val advertisementRepository: AdvertisementRepository by lazy {
        AdvertisementRepositoryImpl(database, appPreferencesManager)
    }

    private val paymentService: PaymentService by lazy {
        ManualPaymentService(appPreferencesManager)
    }

    override val paymentRepository: PaymentRepository by lazy {
        PaymentRepositoryImpl(database, paymentService, appPreferencesManager)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(database)
    }

    override val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl()
    }

    override suspend fun preseedDatabaseIfNeeded(): Unit = withContext(Dispatchers.IO) {
        val jobDao = database.jobDao()
        if (jobDao.getJobCount() == 0) {
            val jobEntities = SeedData.sampleJobs.map { it.toEntity() }
            jobDao.insertJobs(jobEntities)
        }

        val adDao = database.advertisementDao()
        if (adDao.getCount() == 0) {
            val adEntities = SeedData.approvedAds.map { it.toEntity() }
            adDao.insertAll(adEntities)
        }
    }
}
