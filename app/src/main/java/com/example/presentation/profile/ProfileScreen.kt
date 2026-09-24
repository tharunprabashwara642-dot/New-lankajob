package com.example.presentation.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.AppLanguage
import com.example.core.ThemeMode
import com.example.domain.model.UserProfile
import com.example.ui.theme.LankaJobsTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToSignIn: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToMyAds: () -> Unit,
    onNavigateToMyPayments: () -> Unit,
    onNavigateToSavedJobs: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = LankaJobsTheme.tokens

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header
        Surface(
            color = tokens.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "My Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = tokens.textPrimary
                )
            }
        }

        if (uiState.isSignedIn && uiState.userProfile != null) {
            SignedInContent(
                profile = uiState.userProfile!!,
                themeMode = uiState.themeMode,
                language = uiState.language,
                onSetThemeMode = { viewModel.setThemeMode(it) },
                onSetLanguage = { viewModel.setLanguage(it) },
                onEditProfile = onNavigateToEditProfile,
                onMyAds = onNavigateToMyAds,
                onMyPayments = onNavigateToMyPayments,
                onSavedJobs = onNavigateToSavedJobs,
                onNotifications = onNavigateToNotifications,
                onSignOut = { viewModel.signOut { onSignedOut() } }
            )
        } else {
            SignedOutContent(
                isLoading = uiState.isLoading,
                themeMode = uiState.themeMode,
                language = uiState.language,
                onSetThemeMode = { viewModel.setThemeMode(it) },
                onSetLanguage = { viewModel.setLanguage(it) },
                onSignInClick = onNavigateToSignIn
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SignedInContent(
    profile: UserProfile,
    themeMode: ThemeMode,
    language: AppLanguage,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onEditProfile: () -> Unit,
    onMyAds: () -> Unit,
    onMyPayments: () -> Unit,
    onSavedJobs: () -> Unit,
    onNotifications: () -> Unit,
    onSignOut: () -> Unit
) {
    val tokens = LankaJobsTheme.tokens
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = tokens.surface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(tokens.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.fullName.take(2).uppercase().ifBlank { "LJ" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = profile.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    }

                    OutlinedButton(
                        onClick = onEditProfile,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (profile.phoneNumber.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = tokens.textMuted
                        )
                        Text(
                            text = profile.phoneNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    }
                }

                if (profile.preferredLocation.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = tokens.textMuted
                        )
                        Text(
                            text = "Preferred Location: ${profile.preferredLocation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    }
                }

                if (profile.preferredCategories.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        profile.preferredCategories.forEach { cat ->
                            Surface(
                                color = tokens.primaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.primary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Menu Items (Section 20 of specification)
        Card(
            colors = CardDefaults.cardColors(containerColor = tokens.surface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Default.Campaign,
                    title = "My Advertisements",
                    subtitle = "Track approval and live status of your ads",
                    onClick = onMyAds,
                    testTag = "menu_my_advertisements"
                )
                ProfileMenuDivider()
                ProfileMenuItem(
                    icon = Icons.Default.Receipt,
                    title = "My Payments",
                    subtitle = "View manual bank slip submissions & verification status",
                    onClick = onMyPayments,
                    testTag = "menu_my_payments"
                )
                ProfileMenuDivider()
                ProfileMenuItem(
                    icon = Icons.Default.Bookmark,
                    title = "Saved Jobs",
                    subtitle = "View offline and bookmarked career opportunities",
                    onClick = onSavedJobs,
                    testTag = "menu_saved_jobs"
                )
                ProfileMenuDivider()
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notification Settings",
                    subtitle = "Manage job alert preferences and announcements",
                    onClick = onNotifications,
                    testTag = "menu_notifications"
                )
            }
        }

        // Appearance & Language Card
        Card(
            colors = CardDefaults.cardColors(containerColor = tokens.surface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
            modifier = Modifier.fillMaxWidth().testTag("app_preferences_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = tokens.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Theme & Appearance",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Display Theme Mode:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        val isSelected = themeMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSetThemeMode(mode) },
                            label = {
                                Text(
                                    text = when (mode) {
                                        ThemeMode.LIGHT -> "☀️ Light"
                                        ThemeMode.DARK -> "🌙 Dark"
                                        ThemeMode.SYSTEM -> "⚙️ System"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tokens.primaryContainer,
                                selectedLabelColor = tokens.primary
                            ),
                            modifier = Modifier.weight(1f).testTag("theme_chip_${mode.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Preferred Language:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.textSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = language == lang
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSetLanguage(lang) },
                            label = {
                                Text(
                                    text = lang.nativeName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tokens.primaryContainer,
                                selectedLabelColor = tokens.primary
                            ),
                            modifier = Modifier.weight(1f).testTag("lang_chip_${lang.code}")
                        )
                    }
                }
            }
        }

        // Help, Privacy, Terms Card
        Card(
            colors = CardDefaults.cardColors(containerColor = tokens.surface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Help & Support",
                    subtitle = "Contact LankaJobs support team",
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@lankajobs.lk"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    testTag = "menu_help"
                )
                ProfileMenuDivider()
                ProfileMenuItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "Learn how we protect and handle your information",
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lankajobs.lk/privacy"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    testTag = "menu_privacy"
                )
                ProfileMenuDivider()
                ProfileMenuItem(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    subtitle = "Platform conditions and advertising terms",
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lankajobs.lk/terms"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                    testTag = "menu_terms"
                )
            }
        }

        // Sign Out Button
        OutlinedButton(
            onClick = onSignOut,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = tokens.error),
            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.error.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("sign_out_button")
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out of LankaJobs", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SignedOutContent(
    isLoading: Boolean,
    themeMode: ThemeMode,
    language: AppLanguage,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onSignInClick: () -> Unit
) {
    val tokens = LankaJobsTheme.tokens

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(tokens.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = tokens.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to LankaJobs",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = tokens.textPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Sign in with your Google account to save jobs, manage your applicant profile, and post verified sponsored advertisements.",
            style = MaterialTheme.typography.bodyMedium,
            color = tokens.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSignInClick,
            enabled = !isLoading,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("profile_sign_in_button"),
            colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "Sign in with Google",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Appearance settings even when signed out
        Card(
            colors = CardDefaults.cardColors(containerColor = tokens.surface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Display Theme",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = tokens.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        val isSelected = themeMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSetThemeMode(mode) },
                            label = {
                                Text(
                                    text = when (mode) {
                                        ThemeMode.LIGHT -> "☀️ Light"
                                        ThemeMode.DARK -> "🌙 Dark"
                                        ThemeMode.SYSTEM -> "⚙️ System"
                                    },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    val tokens = LankaJobsTheme.tokens

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(tokens.primaryContainer.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tokens.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = tokens.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = tokens.textMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ProfileMenuDivider() {
    val tokens = LankaJobsTheme.tokens
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = tokens.border
    )
}
