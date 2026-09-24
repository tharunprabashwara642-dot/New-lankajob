package com.example.presentation.ads

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.SeedData
import com.example.domain.model.AdvertisementPackage
import com.example.presentation.components.LankaJobsTopBar
import com.example.ui.theme.LankaJobsTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateAdScreen(
    viewModel: AdsViewModel,
    onSuccess: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = LankaJobsTheme.tokens
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Steps:
    // 0: Ad Details
    // 1: Select Package
    // 2: Review Advertisement
    // 3: Payment Instructions & Slip Upload
    // 4: Submission Confirmation ("Payment submitted for verification")
    var currentStep by remember { mutableIntStateOf(0) }

    // Form inputs
    var title by remember { mutableStateOf("") }
    var orgName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Education & Training") }
    var location by remember { mutableStateOf("Colombo") }
    var contactName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }
    var adImageUri by remember { mutableStateOf<Uri?>(null) }

    val packages = uiState.packages
    var selectedPackage by remember(packages) {
        mutableStateOf(packages.firstOrNull { it.isPopular } ?: packages.firstOrNull())
    }

    // Payment details
    var paymentReference by remember { mutableStateOf("") }
    var paymentDate by remember {
        mutableStateOf(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()))
    }
    var receiptImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadingReceipt by remember { mutableStateOf(false) }

    // Image Pickers
    val adImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            adImageUri = uri
            Toast.makeText(context, "Advertisement graphic selected", Toast.LENGTH_SHORT).show()
        }
    }

    val receiptImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            receiptImageUri = uri
            isUploadingReceipt = false
            Toast.makeText(context, "Payment slip selected for verification", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            LankaJobsTopBar(
                title = when (currentStep) {
                    0 -> "Advertisement Details"
                    1 -> "Select Package"
                    2 -> "Review Advertisement"
                    3 -> "Manual Payment"
                    else -> "Verification Status"
                },
                onBackClick = if (currentStep in 1..3) {
                    { currentStep -= 1 }
                } else if (currentStep == 0) {
                    onBackClick
                } else {
                    null
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(tokens.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Step Progress Indicator (Steps 0 to 3)
            if (currentStep < 4) {
                Surface(
                    color = tokens.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StepIndicatorItem(number = 1, label = "Details", isActive = currentStep == 0, isCompleted = currentStep > 0, tokens = tokens)
                            StepIndicatorItem(number = 2, label = "Package", isActive = currentStep == 1, isCompleted = currentStep > 1, tokens = tokens)
                            StepIndicatorItem(number = 3, label = "Review", isActive = currentStep == 2, isCompleted = currentStep > 2, tokens = tokens)
                            StepIndicatorItem(number = 4, label = "Payment", isActive = currentStep == 3, isCompleted = currentStep > 3, tokens = tokens)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Error banner if any
            uiState.errorMessage?.let { errorText ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = tokens.error.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = tokens.error, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = errorText, style = MaterialTheme.typography.bodySmall, color = tokens.error)
                    }
                }
            }

            // Step Content
            when (currentStep) {
                0 -> {
                    // STEP 0: Ad Details Form
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Promote Your Organization or Service",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary
                        )
                        Text(
                            text = "Sponsored advertisements appear prominently on the LankaJobs feed and search.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Advertisement Headline / Title *") },
                            placeholder = { Text("e.g. Diploma in IT & Software Intake 2026") },
                            leadingIcon = { Icon(Icons.Filled.Title, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ad_title_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = orgName,
                            onValueChange = { orgName = it },
                            label = { Text("Business / Institution Name *") },
                            placeholder = { Text("e.g. Colombo Institute of Technology") },
                            leadingIcon = { Icon(Icons.Filled.Business, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ad_org_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Detailed Description *") },
                            placeholder = { Text("Describe the offering, benefits, enrollment details, or promotional terms...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("ad_desc_input"),
                            maxLines = 5
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Chips
                        Text(
                            text = "Target Category",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.textPrimary
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            SeedData.categories.forEach { cat ->
                                FilterChip(
                                    selected = category == cat.name,
                                    onClick = { category = cat.name },
                                    label = { Text(cat.name, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = tokens.primaryContainer,
                                        selectedLabelColor = tokens.primary
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Location
                        Text(
                            text = "Target Location",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.textPrimary
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            SeedData.locations.forEach { loc ->
                                FilterChip(
                                    selected = location == loc,
                                    onClick = { location = loc },
                                    label = { Text(loc, fontSize = 12.sp) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = contactName,
                            onValueChange = { contactName = it },
                            label = { Text("Contact Person / Department") },
                            placeholder = { Text("e.g. Admissions Office") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Official Contact Phone *") },
                            placeholder = { Text("e.g. 077 123 4567") },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ad_phone_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Official Contact Email (Optional)") },
                            placeholder = { Text("admissions@institute.lk") },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = websiteUrl,
                            onValueChange = { websiteUrl = it },
                            label = { Text("Official Website URL (Optional)") },
                            placeholder = { Text("https://www.yourdomain.lk") },
                            leadingIcon = { Icon(Icons.Filled.Link, contentDescription = null) },
                            supportingText = { Text("If provided, a 'Visit Official Website' action will be displayed.") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Ad Image Section
                        Text(
                            text = "Advertisement Banner / Graphic (Optional)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (adImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, tokens.border, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = adImageUri,
                                    contentDescription = "Ad banner preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = { adImageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                        .size(32.dp)
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "Remove image", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = { adImagePicker.launch("image/*") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Choose Image (JPG, PNG, WebP)")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (title.trim().length < 3) {
                                    Toast.makeText(context, "Please enter a valid title", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (orgName.trim().isBlank()) {
                                    Toast.makeText(context, "Please enter your organization name", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (description.trim().length < 10) {
                                    Toast.makeText(context, "Description must be at least 10 characters", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                currentStep = 1
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("continue_to_package_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
                        ) {
                            Text("Continue to Package Selection", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                1 -> {
                    // STEP 1: Select Package
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Select Advertisement Package",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary
                        )
                        Text(
                            text = "Packages are dynamically configured by LankaJobs administration.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        packages.forEach { pkg ->
                            val isSelected = selectedPackage?.id == pkg.id
                            Card(
                                onClick = { selectedPackage = pkg },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) tokens.primaryContainer.copy(alpha = 0.35f) else tokens.surface
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) tokens.primary else tokens.border
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = pkg.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = tokens.textPrimary
                                                )
                                                if (pkg.isPopular) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Surface(
                                                        color = tokens.warning.copy(alpha = 0.2f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = "POPULAR",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = tokens.warning,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = "${pkg.durationDays} Days Active Duration",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = tokens.textSecondary
                                            )
                                        }

                                        Text(
                                            text = pkg.formattedPrice,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = tokens.primary
                                        )
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        color = tokens.border
                                    )

                                    pkg.features.forEach { feat ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = tokens.success,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = feat,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = tokens.textPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { currentStep = 0 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Text("Back")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    if (selectedPackage == null) {
                                        Toast.makeText(context, "Please choose a package", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    currentStep = 2
                                },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(50.dp)
                                    .testTag("continue_to_review_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
                            ) {
                                Text("Review Summary", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                2 -> {
                    // STEP 2: Review Advertisement
                    val pkg = selectedPackage ?: return@Column
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Review Advertisement Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary
                        )
                        Text(
                            text = "Please verify your details before proceeding to payment instructions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = tokens.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.textPrimary
                                )
                                Text(
                                    text = orgName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = tokens.primary,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.textSecondary
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = tokens.border)
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Category:", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                    Text(category, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = tokens.textPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Location:", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                    Text(location, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = tokens.textPrimary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Contact Phone:", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                    Text(phone.ifBlank { "Not provided" }, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = tokens.textPrimary)
                                }
                                if (websiteUrl.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Website:", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                        Text(websiteUrl, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = tokens.primary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    color = tokens.primaryContainer.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Selected Package", fontSize = 11.sp, color = tokens.textSecondary)
                                            Text(pkg.name, fontWeight = FontWeight.Bold, color = tokens.textPrimary)
                                            Text("${pkg.durationDays} Days Duration", fontSize = 11.sp, color = tokens.textSecondary)
                                        }
                                        Text(
                                            pkg.formattedPrice,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = tokens.primary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { currentStep = 1 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Text("Back")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { currentStep = 3 },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(50.dp)
                                    .testTag("continue_to_payment_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
                            ) {
                                Text("Continue to Payment", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                3 -> {
                    // STEP 3: Payment Instructions & Receipt Upload
                    val pkg = selectedPackage ?: return@Column
                    val instructions = uiState.paymentInstructions

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Manual Payment Instructions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary
                        )
                        Text(
                            text = "Deposit or transfer the exact package amount to the official LankaJobs bank account below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        // Amount to transfer banner
                        Card(
                            colors = CardDefaults.cardColors(containerColor = tokens.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.primary.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Total Amount to Transfer", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                        Text(
                                            text = pkg.formattedPrice,
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = tokens.primary
                                        )
                                    }
                                    Surface(
                                        color = tokens.primaryContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = pkg.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Official Bank Details Card (Configured from Admin Web)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = tokens.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = tokens.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Official Bank Account Details", fontWeight = FontWeight.Bold, color = tokens.textPrimary)
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                DetailRow(label = "Bank Name", value = instructions.bankName, tokens = tokens)
                                DetailRow(label = "Branch", value = "${instructions.branch} (Code: ${instructions.branchCode})", tokens = tokens)
                                DetailRow(label = "Account Holder", value = instructions.accountHolderName, tokens = tokens)

                                // Account Number with Copy Button
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = tokens.surfaceElevated,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Account Number", fontSize = 11.sp, color = tokens.textSecondary)
                                            Text(
                                                instructions.accountNumber,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 18.sp,
                                                color = tokens.primary
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(instructions.accountNumber))
                                                Toast.makeText(context, "Account number copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Copy", fontSize = 12.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = instructions.paymentInstructions,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ℹ️ ${instructions.paymentNote}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = tokens.warning
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Transaction Reference & Date Inputs
                        Text(
                            text = "Payment Slip & Submission Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = paymentReference,
                            onValueChange = { paymentReference = it },
                            label = { Text("Transaction Reference / Slip No. *") },
                            placeholder = { Text("e.g. TXN98765432 or Deposit Slip #") },
                            leadingIcon = { Icon(Icons.Filled.Receipt, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("payment_ref_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = paymentDate,
                            onValueChange = { paymentDate = it },
                            label = { Text("Date of Transfer / Deposit *") },
                            leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Receipt Slip Upload Section
                        Text(
                            text = "Upload Bank Deposit Slip / Receipt *",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (receiptImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, tokens.success, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = receiptImageUri,
                                    contentDescription = "Payment Slip Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Slip attached", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Row {
                                        Text(
                                            "Replace",
                                            color = tokens.primary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clickable { receiptImagePicker.launch("image/*") }
                                                .padding(horizontal = 8.dp)
                                        )
                                        Text(
                                            "Remove",
                                            color = tokens.error,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clickable { receiptImageUri = null }
                                                .padding(horizontal = 8.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = { receiptImagePicker.launch("image/*") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("upload_receipt_btn"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(20.dp), tint = tokens.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Select Deposit Slip (JPG, PNG, WebP)", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        if (isUploadingReceipt) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (paymentReference.isBlank()) {
                                    Toast.makeText(context, "Please enter your transaction reference / slip number", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (receiptImageUri == null) {
                                    Toast.makeText(context, "Please attach your bank deposit slip or receipt", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                viewModel.submitAdvertisement(
                                    title = title,
                                    organizationName = orgName,
                                    description = description,
                                    category = category,
                                    location = location,
                                    contactName = contactName,
                                    phone = phone,
                                    email = email,
                                    websiteUrl = websiteUrl,
                                    adImageUrl = adImageUri?.toString(),
                                    selectedPackage = pkg,
                                    paymentReference = paymentReference,
                                    paymentDate = paymentDate,
                                    receiptUrl = receiptImageUri.toString(),
                                    onSuccess = {
                                        currentStep = 4
                                    }
                                )
                            },
                            enabled = !uiState.isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("submit_payment_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
                        ) {
                            if (uiState.isSubmitting) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submitting for Verification...")
                            } else {
                                Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit Payment for Verification", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                4 -> {
                    // STEP 4: Post-submission Screen ("Payment submitted for verification")
                    val pkg = selectedPackage
                    val submissionTime = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(tokens.warning.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.HourglassEmpty,
                                contentDescription = null,
                                tint = tokens.warning,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Payment Submitted for Verification",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = tokens.textPrimary
                        )

                        Text(
                            text = "Your payment reference and deposit receipt have been submitted to LankaJobs administration. Payment is pending manual verification.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = tokens.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DetailRow(label = "Package", value = pkg?.name ?: "Selected Package", tokens = tokens)
                                DetailRow(label = "Amount", value = pkg?.formattedPrice ?: "LKR 0", tokens = tokens)
                                DetailRow(label = "Reference", value = paymentReference, tokens = tokens)
                                DetailRow(label = "Submitted At", value = submissionTime, tokens = tokens)

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = tokens.border)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Payment Status", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                    Surface(
                                        color = tokens.warning.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Pending Verification",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.warning,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Advertisement Status", style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
                                    Surface(
                                        color = tokens.primary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Waiting for Verification",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Process explanation
                        Card(
                            colors = CardDefaults.cardColors(containerColor = tokens.surfaceElevated),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Verification & Publication Workflow:",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "1. LankaJobs admin verifies your bank receipt.\n2. Payment status updates to 'Verified'.\n3. Advertisement is approved and goes live across the app.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.resetSubmissionState()
                                onSuccess(uiState.submittedAd?.id ?: "ad_success")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("view_my_ads_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = tokens.primary)
                        ) {
                            Text("View in My Advertisements", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicatorItem(
    number: Int,
    label: String,
    isActive: Boolean,
    isCompleted: Boolean,
    tokens: com.example.ui.theme.LankaJobsTokens
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = when {
                        isCompleted -> tokens.success
                        isActive -> tokens.primary
                        else -> tokens.border
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    text = number.toString(),
                    color = if (isActive) Color.White else tokens.textSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) tokens.primary else tokens.textSecondary
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    tokens: com.example.ui.theme.LankaJobsTokens
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = tokens.textSecondary)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = tokens.textPrimary)
    }
}
