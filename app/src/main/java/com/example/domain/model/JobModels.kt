package com.example.domain.model

enum class EmploymentType(val label: String) {
    FULL_TIME("Full-time"),
    PART_TIME("Part-time"),
    CONTRACT("Contract"),
    INTERNSHIP("Internship"),
    TEMPORARY("Temporary");

    companion object {
        fun fromString(value: String): EmploymentType =
            entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
                ?: FULL_TIME
    }
}

enum class ExperienceLevel(val label: String) {
    ENTRY_LEVEL("Entry Level"),
    JUNIOR("Junior"),
    MID_LEVEL("Mid Level"),
    SENIOR("Senior"),
    MANAGER("Manager");

    companion object {
        fun fromString(value: String): ExperienceLevel =
            entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
                ?: MID_LEVEL
    }
}

enum class WorkplaceType(val label: String) {
    ON_SITE("On-site"),
    HYBRID("Hybrid"),
    REMOTE("Remote");

    companion object {
        fun fromString(value: String): WorkplaceType =
            entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) }
                ?: ON_SITE
    }
}

enum class JobSortOrder(val label: String) {
    NEWEST("Newest First"),
    SALARY_HIGH_TO_LOW("Highest Salary"),
    RELEVANCE("Most Relevant");
}

data class Job(
    val id: String,
    val title: String,
    val companyId: String,
    val companyName: String,
    val companyLogoUrl: String? = null,
    val categoryId: String,
    val categoryName: String,
    val location: String, // e.g. "Colombo 03", "Kandy"
    val district: String, // e.g. "Colombo", "Kandy", "Galle"
    val employmentType: EmploymentType,
    val experienceLevel: ExperienceLevel,
    val workplaceType: WorkplaceType,
    val salaryMin: Int? = null,
    val salaryMax: Int? = null,
    val salaryCurrency: String = "LKR",
    val isSalaryNegotiable: Boolean = false,
    val description: String,
    val responsibilities: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    val postedDate: String, // e.g. "2 days ago", "Sep 22, 2026"
    val closingDate: String? = null, // e.g. "Oct 15, 2026"
    val isFeatured: Boolean = false,
    val isUrgent: Boolean = false,
    val applicationUrl: String? = null,
    val applicationEmail: String? = null,
    val source: String = "Direct Employer", // e.g. "Direct Employer", "Verified Partner"
    val viewsCount: Int = 0
) {
    val formattedSalary: String
        get() = when {
            salaryMin != null && salaryMax != null -> "$salaryCurrency ${formatNumber(salaryMin)} - ${formatNumber(salaryMax)} /mo"
            salaryMin != null -> "From $salaryCurrency ${formatNumber(salaryMin)} /mo"
            salaryMax != null -> "Up to $salaryCurrency ${formatNumber(salaryMax)} /mo"
            isSalaryNegotiable -> "Negotiable"
            else -> "Undisclosed"
        }

    private fun formatNumber(num: Int): String {
        return "%,d".format(num)
    }
}

data class Company(
    val id: String,
    val name: String,
    val tagline: String,
    val description: String,
    val location: String,
    val websiteUrl: String? = null,
    val industry: String,
    val verified: Boolean = true,
    val logoUrl: String? = null
)

data class JobCategory(
    val id: String,
    val name: String,
    val jobCount: Int = 0,
    val iconIdentifier: String = "work"
)

data class JobFilter(
    val query: String = "",
    val categoryId: String? = null,
    val location: String? = null,
    val employmentType: EmploymentType? = null,
    val experienceLevel: ExperienceLevel? = null,
    val workplaceType: WorkplaceType? = null,
    val minSalary: Int? = null,
    val onlyFeatured: Boolean = false,
    val onlyUrgent: Boolean = false,
    val sortOrder: JobSortOrder = JobSortOrder.NEWEST
) {
    val activeFilterCount: Int
        get() {
            var count = 0
            if (!categoryId.isNullOrBlank()) count++
            if (!location.isNullOrBlank()) count++
            if (employmentType != null) count++
            if (experienceLevel != null) count++
            if (workplaceType != null) count++
            if (minSalary != null && minSalary > 0) count++
            if (onlyFeatured) count++
            if (onlyUrgent) count++
            if (sortOrder != JobSortOrder.NEWEST) count++
            return count
        }
}
