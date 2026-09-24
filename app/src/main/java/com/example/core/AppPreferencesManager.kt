package com.example.core

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.model.AppSettings
import com.example.domain.model.PaymentInstructions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val label: String, val sinhalaLabel: String) {
    SYSTEM("System Default", "පද්ධති සැකසුම"),
    LIGHT("Light Mode", "ලා පැහැය"),
    DARK("Dark Mode", "අඳුරු පැහැය (Dark)")
}

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    SINHALA("si", "Sinhala", "සිංහල"),
    TAMIL("ta", "Tamil", "தமிழ்")
}

class AppPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lankajobs_preferences", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(loadLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _paymentInstructions = MutableStateFlow(loadPaymentInstructions())
    val paymentInstructions: StateFlow<PaymentInstructions> = _paymentInstructions.asStateFlow()

    private val _appSettings = MutableStateFlow(loadAppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private fun loadThemeMode(): ThemeMode {
        val saved = prefs.getString("key_theme_mode", ThemeMode.LIGHT.name) ?: ThemeMode.LIGHT.name
        return try {
            ThemeMode.valueOf(saved)
        } catch (_: Exception) {
            ThemeMode.LIGHT
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("key_theme_mode", mode.name).apply()
    }

    private fun loadLanguage(): AppLanguage {
        val saved = prefs.getString("key_language", AppLanguage.ENGLISH.name) ?: AppLanguage.ENGLISH.name
        return try {
            AppLanguage.valueOf(saved)
        } catch (_: Exception) {
            AppLanguage.ENGLISH
        }
    }

    fun setLanguage(language: AppLanguage) {
        _language.value = language
        prefs.edit().putString("key_language", language.name).apply()
    }

    // --- Dynamic Advertisement Package Prices & Configuration ---
    fun getPackagePrice(packageId: String, defaultPrice: Int): Int {
        return prefs.getInt("key_pkg_price_$packageId", defaultPrice)
    }

    fun setPackagePrice(packageId: String, newPrice: Int) {
        prefs.edit().putInt("key_pkg_price_$packageId", newPrice).apply()
    }

    fun getPackageDuration(packageId: String, defaultDays: Int): Int {
        return prefs.getInt("key_pkg_days_$packageId", defaultDays)
    }

    fun setPackageDuration(packageId: String, days: Int) {
        prefs.edit().putInt("key_pkg_days_$packageId", days).apply()
    }

    // --- Payment Instructions (Backend-Driven Manual Payment Info) ---
    private fun loadPaymentInstructions(): PaymentInstructions {
        return PaymentInstructions(
            bankName = prefs.getString("key_bank_name", "Bank of Ceylon / Commercial Bank") ?: "Bank of Ceylon / Commercial Bank",
            branch = prefs.getString("key_branch", "Corporate Branch, Colombo") ?: "Corporate Branch, Colombo",
            branchCode = prefs.getString("key_branch_code", "001") ?: "001",
            accountHolderName = prefs.getString("key_acc_holder", "LankaJobs (Pvt) Ltd") ?: "LankaJobs (Pvt) Ltd",
            accountNumber = prefs.getString("key_acc_number", "8012345678") ?: "8012345678",
            paymentInstructions = prefs.getString(
                "key_pay_instructions",
                "Transfer the exact package amount via online banking or bank deposit slip. Upload your payment receipt and enter the transaction reference."
            ) ?: "Transfer the exact package amount via online banking or bank deposit slip. Upload your payment receipt and enter the transaction reference.",
            paymentNote = prefs.getString(
                "key_pay_note",
                "Verification takes between 15-60 minutes during business hours."
            ) ?: "Verification takes between 15-60 minutes during business hours.",
            qrCodeUrl = prefs.getString("key_pay_qr", null)
        )
    }

    fun getPaymentInstructions(): PaymentInstructions = _paymentInstructions.value

    fun setPaymentInstructions(instructions: PaymentInstructions) {
        _paymentInstructions.value = instructions
        prefs.edit()
            .putString("key_bank_name", instructions.bankName)
            .putString("key_branch", instructions.branch)
            .putString("key_branch_code", instructions.branchCode)
            .putString("key_acc_holder", instructions.accountHolderName)
            .putString("key_acc_number", instructions.accountNumber)
            .putString("key_pay_instructions", instructions.paymentInstructions)
            .putString("key_pay_note", instructions.paymentNote)
            .putString("key_pay_qr", instructions.qrCodeUrl)
            .apply()
    }

    // --- App Settings & Remote Flags ---
    private fun loadAppSettings(): AppSettings {
        return AppSettings(
            appName = prefs.getString("key_app_name", "LankaJobs") ?: "LankaJobs",
            supportEmail = prefs.getString("key_support_email", "support@lankajobs.lk") ?: "support@lankajobs.lk",
            supportPhone = prefs.getString("key_support_phone", "+94 11 234 5678") ?: "+94 11 234 5678",
            announcementBanner = prefs.getString("key_announcement_banner", null),
            maintenanceMode = prefs.getBoolean("key_maintenance_mode", false),
            privacyUrl = prefs.getString("key_privacy_url", "https://lankajobs.lk/privacy") ?: "https://lankajobs.lk/privacy",
            termsUrl = prefs.getString("key_terms_url", "https://lankajobs.lk/terms") ?: "https://lankajobs.lk/terms"
        )
    }

    fun getAppSettings(): AppSettings = _appSettings.value

    fun setAppSettings(settings: AppSettings) {
        _appSettings.value = settings
        prefs.edit()
            .putString("key_app_name", settings.appName)
            .putString("key_support_email", settings.supportEmail)
            .putString("key_support_phone", settings.supportPhone)
            .putString("key_announcement_banner", settings.announcementBanner)
            .putBoolean("key_maintenance_mode", settings.maintenanceMode)
            .putString("key_privacy_url", settings.privacyUrl)
            .putString("key_terms_url", settings.termsUrl)
            .apply()
    }

    // --- Supabase Cloud Configuration ---
    fun getSupabaseUrl(): String {
        return prefs.getString("key_supabase_url", "https://xyzcompany.supabase.co") ?: "https://xyzcompany.supabase.co"
    }

    fun setSupabaseUrl(url: String) {
        prefs.edit().putString("key_supabase_url", url.trim()).apply()
    }

    fun getSupabaseAnonKey(): String {
        return prefs.getString("key_supabase_anon_key", "") ?: ""
    }

    fun setSupabaseAnonKey(key: String) {
        prefs.edit().putString("key_supabase_anon_key", key.trim()).apply()
    }

    fun isSupabaseConnected(): Boolean {
        return getSupabaseAnonKey().isNotBlank() && getSupabaseUrl().startsWith("https://")
    }
}
