package com.example

import com.example.core.util.ValidationUtil
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvertisementValidationTest {

    @Test
    fun sriLankanPhoneValidation_validCases() {
        assertTrue(ValidationUtil.isValidSriLankanPhone("077 123 4567"))
        assertTrue(ValidationUtil.isValidSriLankanPhone("071-2345678"))
        assertTrue(ValidationUtil.isValidSriLankanPhone("+94 77 123 4567"))
        assertTrue(ValidationUtil.isValidSriLankanPhone("011 234 5678")) // Colombo landline
    }

    @Test
    fun sriLankanPhoneValidation_invalidCases() {
        assertFalse(ValidationUtil.isValidSriLankanPhone(""))
        assertFalse(ValidationUtil.isValidSriLankanPhone("12345"))
        assertFalse(ValidationUtil.isValidSriLankanPhone("079 123 4567")) // invalid mobile prefix 79
        assertFalse(ValidationUtil.isValidSriLankanPhone("abc0771234567"))
    }

    @Test
    fun emailValidation_validCases() {
        assertTrue(ValidationUtil.isValidEmail("admissions@colombotech.lk"))
        assertTrue(ValidationUtil.isValidEmail("info@lankajobs.lk"))
        assertTrue(ValidationUtil.isValidEmail("user.name@gmail.com"))
    }

    @Test
    fun emailValidation_invalidCases() {
        assertFalse(ValidationUtil.isValidEmail(""))
        assertFalse(ValidationUtil.isValidEmail("invalid-email"))
        assertFalse(ValidationUtil.isValidEmail("@nodomain.com"))
    }

    @Test
    fun fullNameValidation() {
        assertTrue(ValidationUtil.validateFullName("Kamal Perera").isValid)
        assertFalse(ValidationUtil.validateFullName("").isValid)
        assertFalse(ValidationUtil.validateFullName("A").isValid)
    }

    @Test
    fun categoriesAndLocationValidation() {
        assertTrue(ValidationUtil.validateCategories(listOf("IT & Software")).isValid)
        assertFalse(ValidationUtil.validateCategories(emptyList()).isValid)

        assertTrue(ValidationUtil.validateLocation("Colombo").isValid)
        assertFalse(ValidationUtil.validateLocation("  ").isValid)
    }
}
