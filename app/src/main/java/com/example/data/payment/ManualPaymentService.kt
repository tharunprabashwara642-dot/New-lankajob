package com.example.data.payment

import com.example.core.AppPreferencesManager
import com.example.domain.model.PaymentInstructions
import com.example.domain.model.PaymentRecord
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ManualPaymentService(
    private val appPreferencesManager: AppPreferencesManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PaymentService {

    override val serviceName: String = "Manual Bank Deposit & Slip Transfer"

    override suspend fun getPaymentInstructions(): PaymentInstructions = withContext(ioDispatcher) {
        appPreferencesManager.getPaymentInstructions()
    }

    override suspend fun processPaymentSubmission(payment: PaymentRecord): Result<String> = withContext(ioDispatcher) {
        try {
            if (payment.amountLkr <= 0) {
                return@withContext Result.failure(IllegalArgumentException("Payment amount must be greater than zero."))
            }
            if (payment.paymentReference.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Payment transaction reference / slip number is required."))
            }
            if (payment.receiptUrl.isNullOrBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Please upload your bank deposit slip or transfer receipt before submitting."))
            }
            Result.success(payment.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
