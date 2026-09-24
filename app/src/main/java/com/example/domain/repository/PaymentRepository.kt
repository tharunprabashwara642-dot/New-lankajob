package com.example.domain.repository

import com.example.domain.model.PaymentInstructions
import com.example.domain.model.PaymentRecord
import com.example.domain.model.PaymentStatus
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getPaymentInstructions(): Flow<PaymentInstructions>
    suspend fun updatePaymentInstructions(instructions: PaymentInstructions): Result<Unit>
    fun getUserPayments(userId: String): Flow<List<PaymentRecord>>
    suspend fun getPaymentByAdId(adId: String): PaymentRecord?
    suspend fun submitPayment(payment: PaymentRecord): Result<String>
    suspend fun updatePaymentStatus(paymentId: String, status: PaymentStatus, reason: String? = null): Result<Unit>
}
