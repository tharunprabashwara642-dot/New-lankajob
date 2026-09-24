package com.example.data.mapper

import com.example.data.local.entity.AdvertisementEntity
import com.example.data.local.entity.JobEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.domain.model.AdStatus
import com.example.domain.model.Advertisement
import com.example.domain.model.EmploymentType
import com.example.domain.model.ExperienceLevel
import com.example.domain.model.Job
import com.example.domain.model.PaymentRecord
import com.example.domain.model.PaymentStatus
import com.example.domain.model.UserProfile
import com.example.domain.model.WorkplaceType

fun JobEntity.toDomain(): Job = Job(
    id = id,
    title = title,
    companyId = companyId,
    companyName = companyName,
    companyLogoUrl = companyLogoUrl,
    categoryId = categoryId,
    categoryName = categoryName,
    location = location,
    district = district,
    employmentType = EmploymentType.fromString(employmentType),
    experienceLevel = ExperienceLevel.fromString(experienceLevel),
    workplaceType = WorkplaceType.fromString(workplaceType),
    salaryMin = salaryMin,
    salaryMax = salaryMax,
    salaryCurrency = salaryCurrency,
    isSalaryNegotiable = isSalaryNegotiable,
    description = description,
    responsibilities = if (responsibilitiesJoined.isBlank()) emptyList() else responsibilitiesJoined.split(";;;"),
    requirements = if (requirementsJoined.isBlank()) emptyList() else requirementsJoined.split(";;;"),
    benefits = if (benefitsJoined.isBlank()) emptyList() else benefitsJoined.split(";;;"),
    postedDate = postedDate,
    closingDate = closingDate,
    isFeatured = isFeatured,
    isUrgent = isUrgent,
    applicationUrl = applicationUrl,
    applicationEmail = applicationEmail,
    source = source,
    viewsCount = viewsCount
)

fun Job.toEntity(): JobEntity = JobEntity(
    id = id,
    title = title,
    companyId = companyId,
    companyName = companyName,
    companyLogoUrl = companyLogoUrl,
    categoryId = categoryId,
    categoryName = categoryName,
    location = location,
    district = district,
    employmentType = employmentType.name,
    experienceLevel = experienceLevel.name,
    workplaceType = workplaceType.name,
    salaryMin = salaryMin,
    salaryMax = salaryMax,
    salaryCurrency = salaryCurrency,
    isSalaryNegotiable = isSalaryNegotiable,
    description = description,
    responsibilitiesJoined = responsibilities.joinToString(";;;"),
    requirementsJoined = requirements.joinToString(";;;"),
    benefitsJoined = benefits.joinToString(";;;"),
    postedDate = postedDate,
    closingDate = closingDate,
    isFeatured = isFeatured,
    isUrgent = isUrgent,
    applicationUrl = applicationUrl,
    applicationEmail = applicationEmail,
    source = source,
    viewsCount = viewsCount
)

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    id = id,
    email = email,
    fullName = fullName,
    phoneNumber = phoneNumber,
    preferredLocation = preferredLocation,
    preferredCategories = if (preferredCategoriesJoined.isBlank()) emptyList() else preferredCategoriesJoined.split(","),
    headline = headline,
    isProfileComplete = isProfileComplete,
    createdAt = updatedAt
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = id,
    email = email,
    fullName = fullName,
    phoneNumber = phoneNumber,
    preferredLocation = preferredLocation,
    preferredCategoriesJoined = preferredCategories.joinToString(","),
    headline = headline,
    isProfileComplete = isProfileComplete,
    updatedAt = createdAt
)

fun AdvertisementEntity.toDomain(): Advertisement = Advertisement(
    id = id,
    title = title,
    description = description,
    organizationName = organizationName,
    imageUrl = imageUrl,
    websiteUrl = websiteUrl,
    contactPhone = contactPhone,
    contactEmail = contactEmail,
    location = location,
    category = category,
    packageId = packageId,
    packageName = packageName,
    status = AdStatus.fromString(status),
    paymentMethod = paymentMethod,
    paymentReference = paymentReference,
    amountPaidLkr = amountPaidLkr,
    rejectionReason = rejectionReason,
    createdAt = createdAt,
    validUntil = validUntil,
    userId = userId
)

fun Advertisement.toEntity(): AdvertisementEntity = AdvertisementEntity(
    id = id,
    title = title,
    description = description,
    organizationName = organizationName,
    imageUrl = imageUrl,
    websiteUrl = websiteUrl,
    contactPhone = contactPhone,
    contactEmail = contactEmail,
    location = location,
    category = category,
    packageId = packageId,
    packageName = packageName,
    status = status.name,
    paymentMethod = paymentMethod,
    paymentReference = paymentReference,
    amountPaidLkr = amountPaidLkr,
    rejectionReason = rejectionReason,
    createdAt = createdAt,
    validUntil = validUntil,
    userId = userId
)

fun PaymentEntity.toDomain(): PaymentRecord = PaymentRecord(
    id = id,
    advertisementId = advertisementId,
    userId = userId,
    packageId = packageId,
    packageName = packageName,
    amountLkr = amountLkr,
    paymentMethod = paymentMethod,
    paymentReference = paymentReference,
    paymentDate = paymentDate,
    receiptUrl = receiptUrl,
    status = PaymentStatus.fromString(status),
    rejectionReason = rejectionReason,
    submittedAt = submittedAt,
    verifiedAt = verifiedAt
)

fun PaymentRecord.toEntity(): PaymentEntity = PaymentEntity(
    id = id,
    advertisementId = advertisementId,
    userId = userId,
    packageId = packageId,
    packageName = packageName,
    amountLkr = amountLkr,
    paymentMethod = paymentMethod,
    paymentReference = paymentReference,
    paymentDate = paymentDate,
    receiptUrl = receiptUrl,
    status = status.name,
    rejectionReason = rejectionReason,
    submittedAt = submittedAt,
    verifiedAt = verifiedAt
)
