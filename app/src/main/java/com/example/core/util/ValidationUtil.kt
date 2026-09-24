package com.example.core.util

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val formattedValue: String? = null
)

object ValidationUtil {

    fun isValidSriLankanPhone(rawPhone: String): Boolean {
        return validateSriLankanPhone(rawPhone).isValid
    }

    fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return trimmed.matches(emailRegex)
    }

    /**
     * Validates Sri Lankan mobile or landline phone numbers strictly:
     * - Mobile: 07[01245678]XXXXXXX (10 digits) or +94 7[01245678]XXXXXXX
     * - Landline: 0[1-9]XXXXXXXX (10 digits) or +94 [1-9]XXXXXXXX
     */
    fun validateSriLankanPhone(rawPhone: String): ValidationResult {
        val trimmed = rawPhone.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult(isValid = false, errorMessage = "Mobile phone number is required.")
        }

        // Remove spaces, hyphens, parentheses, and dots
        val cleaned = trimmed.replace("[\\s\\-\\(\\)\\.]".toRegex(), "")

        // Check if string contains only digits and optional leading +
        if (!cleaned.matches("^\\+?[0-9]+$".toRegex())) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Phone number must contain only numbers (e.g. 077 123 4567)."
            )
        }

        // Normalize to local 9-digit suffix (without leading 0 or +94)
        val normalizedDigits: String = when {
            cleaned.startsWith("+94") -> cleaned.substring(3)
            cleaned.startsWith("0094") -> cleaned.substring(4)
            cleaned.startsWith("94") && cleaned.length == 11 -> cleaned.substring(2)
            cleaned.startsWith("0") && cleaned.length == 10 -> cleaned.substring(1)
            cleaned.length == 9 -> cleaned
            else -> {
                return ValidationResult(
                    isValid = false,
                    errorMessage = "Invalid length. Sri Lankan phone numbers must have 10 digits (e.g. 077 123 4567)."
                )
            }
        }

        // Must be exactly 9 digits after country code / trunk prefix removal
        if (normalizedDigits.length != 9) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Invalid format. Expected 10 digits like 077 123 4567."
            )
        }

        val isMobile = normalizedDigits.matches("^7[01245678][0-9]{7}$".toRegex())
        val isLandline = normalizedDigits.matches("^(?:11|21|23|24|25|26|27|31|32|33|34|35|36|37|38|41|45|47|51|52|54|55|57|63|65|66|67|81|91)[0-9]{7}$".toRegex())

        if (!isMobile && !isLandline) {
            return ValidationResult(
                isValid = false,
                errorMessage = "Invalid Sri Lankan operator code. Mobile numbers must start with 070, 071, 072, 074, 075, 076, 077, or 078."
            )
        }

        val formatted = "0${normalizedDigits.substring(0, 2)} ${normalizedDigits.substring(2, 5)} ${normalizedDigits.substring(5)}"
        return ValidationResult(isValid = true, formattedValue = formatted)
    }

    /**
     * Validates user full name
     */
    fun validateFullName(name: String): ValidationResult {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult(isValid = false, errorMessage = "Full name is required.")
        }
        if (trimmed.length < 3) {
            return ValidationResult(isValid = false, errorMessage = "Name must be at least 3 characters long.")
        }
        if (!trimmed.matches("^[a-zA-Z\\s\\.\\-\\']+$".toRegex())) {
            return ValidationResult(isValid = false, errorMessage = "Name can only contain letters, spaces, and dots.")
        }
        return ValidationResult(isValid = true, formattedValue = trimmed)
    }

    /**
     * Validates preferred categories
     */
    fun validateCategories(categories: List<String>): ValidationResult {
        if (categories.isEmpty()) {
            return ValidationResult(isValid = false, errorMessage = "Please select at least 1 preferred job category.")
        }
        return ValidationResult(isValid = true)
    }

    /**
     * Validates preferred location
     */
    fun validateLocation(location: String): ValidationResult {
        if (location.trim().isEmpty()) {
            return ValidationResult(isValid = false, errorMessage = "Please choose your preferred district or location.")
        }
        return ValidationResult(isValid = true, formattedValue = location.trim())
    }
}
