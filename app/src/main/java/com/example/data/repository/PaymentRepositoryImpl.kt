package com.example.data.repository

import com.example.core.AppPreferencesManager
import com.example.data.local.LankaJobsDatabase
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.data.payment.PaymentService
import com.example.domain.model.PaymentInstructions
import com.example.domain.model.PaymentRecord
import com.example.domain.model.PaymentStatus
import com.example.domain.repository.PaymentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class PaymentRepositoryImpl(
    private val database: LankaJobsDatabase,
    private val paymentService: PaymentService,
    private val appPreferencesManager: AppPreferencesManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PaymentRepository {

    private val paymentDao = database.paymentDao()

    override fun getPaymentInstructions(): Flow<PaymentInstructions> {
        return appPreferencesManager.paymentInstructions
    }

    override suspend fun updatePaymentInstructions(instructions: PaymentInstructions): Result<Unit> = withContext(ioDispatcher) {
        try {
            appPreferencesManager.setPaymentInstructions(instructions)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserPayments(userId: String): Flow<List<PaymentRecord>> {
        return paymentDao.getUserPayments(userId).map { list ->
            list.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override suspend fun getPaymentByAdId(adId: String): PaymentRecord? = withContext(ioDispatcher) {
        paymentDao.getPaymentByAdId(adId)?.toDomain()
    }

    override suspend fun submitPayment(payment: PaymentRecord): Result<String> = withContext(ioDispatcher) {
        try {
            val finalId = if (payment.id.isBlank()) "pay_${UUID.randomUUID().toString().take(8)}" else payment.id
            val prepared = payment.copy(
                id = finalId,
                status = PaymentStatus.SUBMITTED,
                submittedAt = System.currentTimeMillis()
            )
            val validation = paymentService.processPaymentSubmission(prepared)
            if (validation.isFailure) {
                return@withContext Result.failure(validation.exceptionOrNull() ?: Exception("Validation error"))
            }

            paymentDao.insertOrUpdate(prepared.toEntity())
            Result.success(finalId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePaymentStatus(
        paymentId: String,
        status: PaymentStatus,
        reason: String?
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            paymentDao.updateStatusWithReason(paymentId, status.name, reason)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
