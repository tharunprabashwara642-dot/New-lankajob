package com.example.presentation.ads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.util.ValidationUtil
import com.example.domain.model.AdStatus
import com.example.domain.model.Advertisement
import com.example.domain.model.AdvertisementPackage
import com.example.domain.model.PaymentInstructions
import com.example.domain.model.PaymentRecord
import com.example.domain.model.PaymentStatus
import com.example.domain.model.UserProfile
import com.example.domain.repository.AdvertisementRepository
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.PaymentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class AdsUiState(
    val isLoading: Boolean = true,
    val approvedAds: List<Advertisement> = emptyList(),
    val suggestedAds: List<Advertisement> = emptyList(),
    val userAds: List<Advertisement> = emptyList(),
    val userProfile: UserProfile? = null,
    val packages: List<AdvertisementPackage> = emptyList(),
    val paymentInstructions: PaymentInstructions = PaymentInstructions(),
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false,
    val submittedAd: Advertisement? = null,
    val submittedPayment: PaymentRecord? = null,
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class AdsViewModel(
    private val advertisementRepository: AdvertisementRepository,
    private val paymentRepository: PaymentRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _submissionSuccess = MutableStateFlow(false)
    val submissionSuccess: StateFlow<Boolean> = _submissionSuccess.asStateFlow()

    private val _submittedAd = MutableStateFlow<Advertisement?>(null)
    val submittedAd: StateFlow<Advertisement?> = _submittedAd.asStateFlow()

    private val _submittedPayment = MutableStateFlow<PaymentRecord?>(null)
    val submittedPayment: StateFlow<PaymentRecord?> = _submittedPayment.asStateFlow()

    private val suggestedFlow: Flow<List<Advertisement>> = authRepository.currentUser.flatMapLatest { user ->
        val cats = user?.preferredCategories ?: emptyList()
        val loc = user?.preferredLocation ?: ""
        advertisementRepository.getSuggestedAdvertisements(cats, loc)
    }

    val uiState: StateFlow<AdsUiState> = combine(
        advertisementRepository.getApprovedAdvertisements(),
        suggestedFlow,
        authRepository.currentUser,
        paymentRepository.getPaymentInstructions()
    ) { allAds, suggested, user, instructions ->
        val userAdsList = if (user != null) {
            allAds.filter { it.userId == user.id }
        } else {
            emptyList()
        }
        AdsUiState(
            isLoading = false,
            approvedAds = allAds,
            suggestedAds = suggested,
            userAds = userAdsList,
            userProfile = user,
            packages = advertisementRepository.getPackages(),
            paymentInstructions = instructions,
            isSubmitting = _isSubmitting.value,
            submissionSuccess = _submissionSuccess.value,
            submittedAd = _submittedAd.value,
            submittedPayment = _submittedPayment.value,
            errorMessage = _errorMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdsUiState(isLoading = true, packages = advertisementRepository.getPackages())
    )

    fun submitAdvertisement(
        title: String,
        organizationName: String,
        description: String,
        category: String,
        location: String,
        contactName: String,
        phone: String,
        email: String?,
        websiteUrl: String?,
        adImageUrl: String?,
        selectedPackage: AdvertisementPackage,
        paymentReference: String,
        paymentDate: String,
        receiptUrl: String?,
        onSuccess: (adId: String) -> Unit
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            val titleClean = title.trim()
            val orgClean = organizationName.trim()
            val descClean = description.trim()
            val catClean = category.trim()
            val locClean = location.trim()
            val phoneClean = phone.trim()
            val emailClean = email?.trim()?.ifBlank { null }
            val webClean = websiteUrl?.trim()?.ifBlank { null }
            val refClean = paymentReference.trim()
            val receiptClean = receiptUrl?.trim()?.ifBlank { null }

            if (titleClean.length < 3) {
                _errorMessage.value = "Advertisement title must be at least 3 characters."
                _isSubmitting.value = false
                return@launch
            }
            if (orgClean.isBlank()) {
                _errorMessage.value = "Company or Organization name is required."
                _isSubmitting.value = false
                return@launch
            }
            if (descClean.length < 10) {
                _errorMessage.value = "Please provide a descriptive explanation (at least 10 characters)."
                _isSubmitting.value = false
                return@launch
            }
            if (phoneClean.isNotBlank() && !ValidationUtil.isValidSriLankanPhone(phoneClean)) {
                _errorMessage.value = "Please enter a valid Sri Lankan telephone number (e.g. 077 123 4567)."
                _isSubmitting.value = false
                return@launch
            }
            if (emailClean != null && !ValidationUtil.isValidEmail(emailClean)) {
                _errorMessage.value = "Please enter a valid email address."
                _isSubmitting.value = false
                return@launch
            }
            if (webClean != null && !webClean.startsWith("http://") && !webClean.startsWith("https://")) {
                _errorMessage.value = "Website URL must start with http:// or https://"
                _isSubmitting.value = false
                return@launch
            }
            if (refClean.isBlank()) {
                _errorMessage.value = "Bank deposit slip / transaction reference number is required."
                _isSubmitting.value = false
                return@launch
            }
            if (receiptClean == null) {
                _errorMessage.value = "Please upload your payment receipt or transfer slip before submitting."
                _isSubmitting.value = false
                return@launch
            }
            if (selectedPackage.priceLkr <= 0) {
                _errorMessage.value = "Invalid package price."
                _isSubmitting.value = false
                return@launch
            }

            val currentUserId = authRepository.currentUser.firstOrNull()?.id ?: "usr_current"
            val adId = "ad_usr_${UUID.randomUUID().toString().take(8)}"
            val paymentId = "pay_${UUID.randomUUID().toString().take(8)}"

            val newAd = Advertisement(
                id = adId,
                title = titleClean,
                organizationName = orgClean,
                description = descClean,
                category = catClean,
                location = locClean,
                contactPhone = phoneClean.ifBlank { null },
                contactEmail = emailClean,
                websiteUrl = webClean,
                imageUrl = adImageUrl,
                packageId = selectedPackage.id,
                packageName = selectedPackage.name,
                status = AdStatus.PAYMENT_SUBMITTED,
                paymentMethod = "Bank Deposit / Slip",
                paymentReference = refClean,
                amountPaidLkr = selectedPackage.priceLkr,
                createdAt = System.currentTimeMillis(),
                validUntil = System.currentTimeMillis() + (selectedPackage.durationDays * 86400000L),
                userId = currentUserId
            )

            val newPayment = PaymentRecord(
                id = paymentId,
                advertisementId = adId,
                userId = currentUserId,
                packageId = selectedPackage.id,
                packageName = selectedPackage.name,
                amountLkr = selectedPackage.priceLkr,
                paymentMethod = "Bank Deposit / Transfer Slip",
                paymentReference = refClean,
                paymentDate = if (paymentDate.isNotBlank()) paymentDate else SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                receiptUrl = receiptClean,
                status = PaymentStatus.SUBMITTED,
                submittedAt = System.currentTimeMillis()
            )

            val adResult = advertisementRepository.submitAdvertisement(newAd)
            val payResult = paymentRepository.submitPayment(newPayment)

            _isSubmitting.value = false

            if (adResult.isSuccess && payResult.isSuccess) {
                _submittedAd.value = newAd
                _submittedPayment.value = newPayment
                _submissionSuccess.value = true
                onSuccess(adId)
            } else {
                val error = adResult.exceptionOrNull()?.message ?: payResult.exceptionOrNull()?.message ?: "Submission failed"
                _errorMessage.value = error
            }
        }
    }

    fun resetSubmissionState() {
        _submissionSuccess.value = false
        _submittedAd.value = null
        _submittedPayment.value = null
        _errorMessage.value = null
    }
}
