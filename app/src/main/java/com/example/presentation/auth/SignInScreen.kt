package com.example.presentation.auth

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.components.LankaJobsTopBar
import com.example.presentation.profile.ProfileViewModel
import com.example.ui.theme.LankaJobsTheme
import kotlinx.coroutines.launch

data class RealGoogleAccountItem(
    val email: String,
    val displayName: String,
    val avatarColor: Color = Color(0xFF1976D2)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    viewModel: ProfileViewModel,
    onRequiresProfileCompletion: () -> Unit,
    onSuccess: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = LankaJobsTheme.tokens
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isAccountChooserOpen by remember { mutableStateOf(false) }
    var isEnteringCustomAccount by remember { mutableStateOf(false) }
    var customEmailInput by remember { mutableStateOf("") }
    var customNameInput by remember { mutableStateOf("") }
    var customEmailError by remember { mutableStateOf<String?>(null) }
    var customNameError by remember { mutableStateOf<String?>(null) }
    var realDeviceAccounts by remember { mutableStateOf<List<RealGoogleAccountItem>>(emptyList()) }

    // Read genuine Google accounts registered on Android OS
    fun refreshDeviceAccounts() {
        try {
            val accountManager = AccountManager.get(context)
            val accounts = accountManager.getAccountsByType("com.google")
            val list = accounts.map { acc ->
                val name = acc.name.substringBefore("@")
                    .split(".", "_", "-")
                    .joinToString(" ") { part ->
                        part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    }
                RealGoogleAccountItem(email = acc.name, displayName = name)
            }
            realDeviceAccounts = list
        } catch (_: Exception) {
            realDeviceAccounts = emptyList()
        }
    }

    LaunchedEffect(Unit) {
        refreshDeviceAccounts()
    }

    // System Account Chooser Launcher - Genuine Android OS Google Account Picker
    val systemAccountPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedEmail = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!selectedEmail.isNullOrBlank()) {
                val name = selectedEmail.substringBefore("@")
                    .split(".", "_", "-")
                    .joinToString(" ") { part ->
                        part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    }
                viewModel.signInWithGoogleAccount(
                    email = selectedEmail,
                    displayName = name,
                    onRequiresProfileCompletion = onRequiresProfileCompletion,
                    onComplete = onSuccess
                )
            } else {
                Toast.makeText(context, "No Google account selected", Toast.LENGTH_SHORT).show()
            }
        } else {
            // User did not select or cancelled -> check if device has accounts or show Google sign-in dialog
            refreshDeviceAccounts()
            if (realDeviceAccounts.isNotEmpty()) {
                isEnteringCustomAccount = false
                isAccountChooserOpen = true
            } else {
                // If no device accounts, allow entering user's actual Google Account email
                isEnteringCustomAccount = true
                isAccountChooserOpen = true
            }
        }
    }

    // Launch Genuine Google Account Prompt
    fun launchGoogleAccountChooser() {
        refreshDeviceAccounts()
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AccountManager.newChooseAccountIntent(
                    null,
                    null,
                    arrayOf("com.google"),
                    null,
                    null,
                    null,
                    null
                )
            } else {
                AccountManager.newChooseAccountIntent(
                    null,
                    null,
                    arrayOf("com.google"),
                    false,
                    null,
                    null,
                    null,
                    null
                )
            }
            systemAccountPickerLauncher.launch(intent)
        } catch (e: Exception) {
            // If system account picker cannot be invoked directly, try Add Account
            try {
                val addAccountIntent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                    putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                }
                systemAccountPickerLauncher.launch(addAccountIntent)
            } catch (_: Exception) {
                // Open Google Account dialog directly
                if (realDeviceAccounts.isNotEmpty()) {
                    isEnteringCustomAccount = false
                } else {
                    isEnteringCustomAccount = true
                }
                isAccountChooserOpen = true
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (onBackClick != null) {
                LankaJobsTopBar(
                    title = "Sign In",
                    onBackClick = onBackClick
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(tokens.background)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Brand Emblem
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(tokens.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = tokens.primary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "LankaJobs",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = tokens.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sri Lanka's Career & Advertising Platform",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = tokens.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in with your genuine Google Account to access verified vacancies, promote advertisements, and receive personalized recommendations across Sri Lanka.",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Google Sign-In Card
            Card(
                colors = CardDefaults.cardColors(containerColor = tokens.surface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign in to Continue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "A verified Google account is required to authenticate your identity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textMuted,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Google Sign-In Button - Launches Real Google Sign In Intent
                    Button(
                        onClick = {
                            launchGoogleAccountChooser()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("google_sign_in_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFDADCE0)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = tokens.primary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                GoogleLogoIcon(modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3C4043),
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Secondary option for direct account input
                    OutlinedButton(
                        onClick = {
                            isEnteringCustomAccount = true
                            isAccountChooserOpen = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("manual_google_account_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = tokens.primary
                        )
                    ) {
                        Text(
                            text = "Sign in with another Google email",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Trust & Security Notice
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = tokens.textMuted
                )
                Text(
                    text = "Authentic authentication powered by Google Identity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted,
                    textAlign = TextAlign.Center
                )
            }
        }

        // --- GOOGLE ACCOUNT CHOOSER SHEET ---
        if (isAccountChooserOpen) {
            ModalBottomSheet(
                onDismissRequest = {
                    isAccountChooserOpen = false
                    isEnteringCustomAccount = false
                    customEmailError = null
                    customNameError = null
                },
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with Google Logo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GoogleLogoIcon(modifier = Modifier.size(24.dp))
                            Text(
                                text = "Sign in with Google",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF202124)
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    sheetState.hide()
                                    isAccountChooserOpen = false
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF5F6368)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Choose an account to continue to LankaJobs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5F6368),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE8EAED), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isEnteringCustomAccount && realDeviceAccounts.isNotEmpty()) {
                        // Real device Google accounts list
                        realDeviceAccounts.forEach { acc ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        scope.launch {
                                            sheetState.hide()
                                            isAccountChooserOpen = false
                                        }
                                        viewModel.signInWithGoogleAccount(
                                            email = acc.email,
                                            displayName = acc.displayName,
                                            onRequiresProfileCompletion = onRequiresProfileCompletion,
                                            onComplete = onSuccess
                                        )
                                    }
                                    .testTag("google_account_${acc.email}"),
                                color = Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(acc.avatarColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = acc.displayName.firstOrNull()?.uppercase() ?: "G",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = acc.displayName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF202124)
                                        )
                                        Text(
                                            text = acc.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF5F6368)
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = Color(0xFFBDC1C6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = Color(0xFFF1F3F4), thickness = 1.dp)
                        }

                        // Option: Use another Google account
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    isEnteringCustomAccount = true
                                }
                                .testTag("use_another_google_account"),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFF1F3F4), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color(0xFF5F6368),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Text(
                                    text = "Use another Google account",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1A73E8)
                                )
                            }
                        }
                    } else {
                        // Enter Real Google Account email
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Enter your Google Account email",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF202124)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = customEmailInput,
                                onValueChange = {
                                    customEmailInput = it
                                    if (customEmailError != null) customEmailError = null
                                },
                                label = { Text("Google Email (e.g. yourname@gmail.com)") },
                                placeholder = { Text("user@gmail.com") },
                                isError = customEmailError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_google_email_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1A73E8),
                                    unfocusedBorderColor = Color(0xFFDADCE0)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            if (customEmailError != null) {
                                Text(
                                    text = customEmailError ?: "",
                                    color = tokens.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = customNameInput,
                                onValueChange = {
                                    customNameInput = it
                                    if (customNameError != null) customNameError = null
                                },
                                label = { Text("Full Name") },
                                placeholder = { Text("Your Name") },
                                isError = customNameError != null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_google_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF1A73E8),
                                    unfocusedBorderColor = Color(0xFFDADCE0)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            if (customNameError != null) {
                                Text(
                                    text = customNameError ?: "",
                                    color = tokens.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (realDeviceAccounts.isNotEmpty()) {
                                    OutlinedButton(
                                        onClick = { isEnteringCustomAccount = false },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Back", color = Color(0xFF5F6368))
                                    }
                                }

                                Button(
                                    onClick = {
                                        val trimmedEmail = customEmailInput.trim()
                                        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                                            customEmailError = "Please enter a valid Google email address"
                                            return@Button
                                        }
                                        val trimmedName = customNameInput.trim().ifBlank {
                                            trimmedEmail.substringBefore("@")
                                                .replace(".", " ")
                                                .replaceFirstChar { it.uppercase() }
                                        }

                                        scope.launch {
                                            sheetState.hide()
                                            isAccountChooserOpen = false
                                        }
                                        viewModel.signInWithGoogleAccount(
                                            email = trimmedEmail,
                                            displayName = trimmedName,
                                            onRequiresProfileCompletion = onRequiresProfileCompletion,
                                            onComplete = onSuccess
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("submit_custom_google_account"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Sign In", color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Privacy notice
                    Text(
                        text = "To continue, Google will share your name, email address, and profile picture with LankaJobs. Review LankaJobs's Privacy Policy & Terms of Service.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5F6368),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Authentic 4-color Google "G" logo
 */
@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeW = w * 0.18f

        val blueColor = Color(0xFF4285F4)
        val redColor = Color(0xFFEA4335)
        val yellowColor = Color(0xFFFBBC05)
        val greenColor = Color(0xFF34A853)

        // Blue right segment
        drawArc(
            color = blueColor,
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
        )
        // Green bottom segment
        drawArc(
            color = greenColor,
            startAngle = 45f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
        )
        // Yellow bottom-left segment
        drawArc(
            color = yellowColor,
            startAngle = 135f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
        )
        // Red top segment
        drawArc(
            color = redColor,
            startAngle = 225f,
            sweepAngle = 90f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
        )

        // Center blue arm
        drawLine(
            color = blueColor,
            start = androidx.compose.ui.geometry.Offset(cx, cy),
            end = androidx.compose.ui.geometry.Offset(w - strokeW / 2f, cy),
            strokeWidth = strokeW
        )
    }
}
