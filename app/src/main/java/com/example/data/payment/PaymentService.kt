package com.example.data.payment

import com.example.domain.model.PaymentInstructions
import com.example.domain.model.PaymentRecord

/**
 * Extensible abstraction for payment processing.
 * Currently uses [ManualPaymentService]. In the future, this can be swapped
 * or augmented with GatewayPaymentService without rewriting application UI or ad logic.
 */
interface PaymentService {
    val serviceName: String
    suspend fun getPaymentInstructions(): PaymentInstructions
    suspend fun processPaymentSubmission(payment: PaymentRecord): Result<String>
}
