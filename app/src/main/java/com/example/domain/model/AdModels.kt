package com.example.domain.model

enum class AdStatus(val label: String) {
    DRAFT("Draft"),
    PENDING_PAYMENT("Pending Payment"),
    PAYMENT_SUBMITTED("Payment Submitted"),
    PAYMENT_VERIFIED("Payment Verified"),
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Live"),
    REJECTED("Rejected"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): AdStatus =
            entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
                ?: PAYMENT_SUBMITTED
    }
}

enum class PaymentStatus(val label: String) {
    PENDING("Pending"),
    SUBMITTED("Pending Verification"),
    VERIFIED("Verified"),
    REJECTED("Rejected");

    companion object {
        fun fromString(value: String): PaymentStatus =
            entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
                ?: SUBMITTED
    }
}

data class PaymentInstructions(
    val bankName: String = "Bank of Ceylon / Commercial Bank",
    val branch: String = "Corporate Branch, Colombo",
    val branchCode: String = "001",
    val accountHolderName: String = "LankaJobs (Pvt) Ltd",
    val accountNumber: String = "8012345678",
    val paymentInstructions: String = "Transfer the exact package amount via online banking or bank deposit slip. Upload your payment receipt and enter the transaction reference.",
    val paymentNote: String = "Verification takes between 15-60 minutes during business hours.",
    val qrCodeUrl: String? = null,
    val supportedMethods: List<String> = listOf("Direct Bank Deposit", "Online Banking Slip Transfer", "ATM / CDM Deposit")
)

data class AdvertisementPackage(
    val id: String,
    val name: String,
    val priceLkr: Int,
    val durationDays: Int,
    val features: List<String>,
    val isPopular: Boolean = false,
    val isActive: Boolean = true,
    val sortOrder: Int = 0
) {
    val formattedPrice: String
        get() = "LKR %,d".format(priceLkr)
}

data class Advertisement(
    val id: String,
    val title: String,
    val description: String,
    val organizationName: String,
    val imageUrl: String? = null,
    val websiteUrl: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val location: String,
    val category: String,
    val packageId: String,
    val packageName: String = "Standard Package",
    val status: AdStatus = AdStatus.PAYMENT_SUBMITTED,
    val paymentMethod: String? = "Bank Deposit / Slip",
    val paymentReference: String? = null,
    val amountPaidLkr: Int = 0,
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val validUntil: Long? = null,
    val userId: String? = null
)

data class PaymentRecord(
    val id: String,
    val advertisementId: String,
    val userId: String,
    val packageId: String,
    val packageName: String,
    val amountLkr: Int,
    val paymentMethod: String = "Bank Deposit / Slip",
    val paymentReference: String,
    val paymentDate: String,
    val receiptUrl: String? = null,
    val status: PaymentStatus = PaymentStatus.SUBMITTED,
    val rejectionReason: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val verifiedAt: Long? = null
)

data class AppSettings(
    val appName: String = "LankaJobs",
    val supportEmail: String = "support@lankajobs.lk",
    val supportPhone: String = "+94 11 234 5678",
    val announcementBanner: String? = null,
    val maintenanceMode: Boolean = false,
    val privacyUrl: String = "https://lankajobs.lk/privacy",
    val termsUrl: String = "https://lankajobs.lk/terms"
)
