package com.example.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.util.ValidationUtil
import com.example.data.local.SeedData
import com.example.presentation.components.LankaJobsTopBar
import com.example.presentation.profile.ProfileViewModel
import com.example.ui.theme.LankaJobsTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompleteProfileScreen(
    viewModel: ProfileViewModel,
    onProfileCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = LankaJobsTheme.tokens
    val context = LocalContext.current

    var fullName by remember(uiState.userProfile) {
        mutableStateOf(uiState.userProfile?.fullName ?: "")
    }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Colombo") }
    val selectedCategories = remember { mutableStateListOf("IT & Software") }

    var phoneError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LankaJobsTopBar(
                title = "Complete Your Profile"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(tokens.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = tokens.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "A Few Quick Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = "Customize your profile to receive personalized job matches and relevant promotional announcements across Sri Lanka.",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Full Name
                    Text(
                        text = "Full Name *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            if (nameError != null) nameError = null
                        },
                        placeholder = { Text("e.g. Kasun Perera") },
                        isError = nameError != null,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = tokens.textMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complete_name_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    if (nameError != null) {
                        Text(
                            text = nameError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone Number with Strict Sri Lankan Validation
                    Text(
                        text = "Mobile Phone Number *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = "Must be a valid 10-digit Sri Lankan number (e.g. 077 123 4567 or +94 77 123 4567)",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            val validation = ValidationUtil.validateSriLankanPhone(it)
                            phoneError = if (it.isNotBlank() && !validation.isValid) {
                                validation.errorMessage
                            } else {
                                null
                            }
                        },
                        placeholder = { Text("e.g. 077 123 4567") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phoneError != null,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = tokens.textMuted
                            )
                        },
                        trailingIcon = {
                            val res = ValidationUtil.validateSriLankanPhone(phone)
                            if (phone.isNotBlank()) {
                                if (res.isValid) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Valid Phone",
                                        tint = tokens.success,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = "Invalid Phone",
                                        tint = tokens.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complete_phone_input"),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    if (phoneError != null) {
                        Text(
                            text = phoneError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preferred Location
                    Text(
                        text = "Preferred District / City *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = "Advertisements and job suggestions will be tailored to this district",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SeedData.locations.forEach { loc ->
                            FilterChip(
                                selected = location == loc,
                                onClick = { location = loc },
                                label = { Text(loc, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tokens.primaryContainer,
                                    selectedLabelColor = tokens.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Preferred Categories
                    Text(
                        text = "Preferred Job Categories *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = "Select one or more categories for personalized suggestions",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SeedData.categories.forEach { cat ->
                            val isSelected = selectedCategories.contains(cat.name)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        selectedCategories.remove(cat.name)
                                    } else {
                                        selectedCategories.add(cat.name)
                                    }
                                    if (categoryError != null) categoryError = null
                                },
                                label = { Text(cat.name, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tokens.primaryContainer,
                                    selectedLabelColor = tokens.primary
                                )
                            )
                        }
                    }
                    if (categoryError != null) {
                        Text(
                            text = categoryError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    val nameValidation = ValidationUtil.validateFullName(fullName)
                    if (!nameValidation.isValid) {
                        nameError = nameValidation.errorMessage
                        Toast.makeText(context, nameValidation.errorMessage, Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val phoneValidation = ValidationUtil.validateSriLankanPhone(phone)
                    if (!phoneValidation.isValid) {
                        phoneError = phoneValidation.errorMessage
                        Toast.makeText(context, phoneValidation.errorMessage, Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val categoryValidation = ValidationUtil.validateCategories(selectedCategories)
                    if (!categoryValidation.isValid) {
                        categoryError = categoryValidation.errorMessage
                        Toast.makeText(context, categoryValidation.errorMessage, Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val finalPhone = phoneValidation.formattedValue ?: phone.trim()

                    viewModel.completeProfile(
                        fullName = nameValidation.formattedValue ?: fullName.trim(),
                        phone = finalPhone,
                        location = location,
                        categories = selectedCategories.toList(),
                        onSuccess = onProfileCompleted
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_profile_button"),
                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary),
                shape = RoundedCornerShape(10.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Save & Enter LankaJobs",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
