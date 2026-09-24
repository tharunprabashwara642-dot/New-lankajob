package com.example

import com.example.domain.model.AdStatus
import com.example.domain.model.Advertisement
import com.example.domain.model.AdvertisementPackage
import com.example.domain.model.PaymentInstructions
import com.example.domain.model.PaymentRecord
import com.example.domain.model.PaymentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentWorkflowTest {

    @Test
    fun paymentStatusEnum_mapsCorrectly() {
        assertEquals(PaymentStatus.SUBMITTED, PaymentStatus.fromString("SUBMITTED"))
        assertEquals(PaymentStatus.VERIFIED, PaymentStatus.fromString("VERIFIED"))
        assertEquals(PaymentStatus.REJECTED, PaymentStatus.fromString("REJECTED"))
        assertEquals(PaymentStatus.PENDING, PaymentStatus.fromString("PENDING"))
    }

    @Test
    fun adStatusEnum_mapsCorrectly() {
        assertEquals(AdStatus.PAYMENT_SUBMITTED, AdStatus.fromString("PAYMENT_SUBMITTED"))
        assertEquals(AdStatus.PAYMENT_VERIFIED, AdStatus.fromString("PAYMENT_VERIFIED"))
        assertEquals(AdStatus.PENDING_APPROVAL, AdStatus.fromString("PENDING_APPROVAL"))
        assertEquals(AdStatus.APPROVED, AdStatus.fromString("APPROVED"))
        assertEquals(AdStatus.REJECTED, AdStatus.fromString("REJECTED"))
    }

    @Test
    fun manualPaymentWorkflow_stateTransitionSequence() {
        // Step 1: User creates ad & selects package
        val pkg = AdvertisementPackage(
            id = "pkg_standard",
            name = "Professional Banner",
            priceLkr = 6000,
            durationDays = 30,
            features = listOf("Banner placement", "30 days"),
            isPopular = true
        )

        val ad = Advertisement(
            id = "ad_test_1",
            title = "Cloud DevOps Intake 2026",
            organizationName = "Colombo Tech Academy",
            description = "Enroll today for premier DevOps certification",
            category = "IT & Software",
            location = "Colombo",
            contactPhone = "077 123 4567",
            packageId = pkg.id,
            packageName = pkg.name,
            amountPaidLkr = pkg.priceLkr,
            status = AdStatus.PAYMENT_SUBMITTED
        )

        val payment = PaymentRecord(
            id = "pay_test_1",
            advertisementId = ad.id,
            userId = "usr_1",
            packageId = pkg.id,
            packageName = pkg.name,
            amountLkr = pkg.priceLkr,
            paymentReference = "SLIP-998811",
            paymentDate = "24 Sep 2026",
            receiptUrl = "https://supabase.co/storage/v1/object/payment-receipts/slip.png",
            status = PaymentStatus.SUBMITTED
        )

        assertEquals(PaymentStatus.SUBMITTED, payment.status)
        assertEquals(AdStatus.PAYMENT_SUBMITTED, ad.status)

        // Step 2: Admin verifies payment
        val verifiedPayment = payment.copy(
            status = PaymentStatus.VERIFIED,
            verifiedAt = System.currentTimeMillis()
        )
        val adAfterPaymentVerification = ad.copy(
            status = AdStatus.PENDING_APPROVAL
        )

        assertEquals(PaymentStatus.VERIFIED, verifiedPayment.status)
        assertEquals(AdStatus.PENDING_APPROVAL, adAfterPaymentVerification.status)

        // Step 3: Admin approves advertisement
        val approvedAd = adAfterPaymentVerification.copy(
            status = AdStatus.APPROVED,
            validUntil = System.currentTimeMillis() + (pkg.durationDays * 86400000L)
        )

        assertEquals(AdStatus.APPROVED, approvedAd.status)
        assertNotNull(approvedAd.validUntil)
    }

    @Test
    fun paymentRejection_storesReason() {
        val rejectionReason = "Deposit slip reference does not match bank transaction record."
        val payment = PaymentRecord(
            id = "pay_test_2",
            advertisementId = "ad_test_2",
            userId = "usr_2",
            packageId = "pkg_basic",
            packageName = "Starter Listing",
            amountLkr = 2500,
            paymentReference = "INVALID-REF",
            paymentDate = "24 Sep 2026",
            receiptUrl = "https://supabase.co/receipt.png",
            status = PaymentStatus.REJECTED,
            rejectionReason = rejectionReason
        )

        assertEquals(PaymentStatus.REJECTED, payment.status)
        assertEquals(rejectionReason, payment.rejectionReason)
    }

    @Test
    fun paymentInstructions_defaultsValid() {
        val instructions = PaymentInstructions()
        assertTrue(instructions.bankName.isNotBlank())
        assertTrue(instructions.accountNumber.isNotBlank())
        assertTrue(instructions.accountHolderName.isNotBlank())
        assertFalse(instructions.supportedMethods.isEmpty())
    }
}
