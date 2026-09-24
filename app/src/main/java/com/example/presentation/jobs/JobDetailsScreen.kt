package com.example.presentation.jobs

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.Job
import com.example.presentation.components.Badge
import com.example.presentation.components.EmptyState
import com.example.presentation.components.LoadingState
import com.example.ui.theme.LankaJobsTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun JobDetailsScreen(
    viewModel: JobDetailsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = LankaJobsTheme.tokens
    val context = LocalContext.current

    if (uiState.isLoading) {
        LoadingState(modifier = modifier.fillMaxSize())
        return
    }

    val job = uiState.job
    if (job == null) {
        EmptyState(
            title = "Job Listing Not Found",
            subtitle = "This position may have expired or been removed by the employer.",
            actionLabel = "Go Back",
            onActionClick = onBackClick,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Job Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("job_details_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = tokens.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "${job.title} at ${job.companyName}"
                                )
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out this job opportunity in Sri Lanka on LankaJobs:\n\n${job.title} at ${job.companyName}\nLocation: ${job.location}\nSalary: ${job.formattedSalary}"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Job Opportunity"))
                        },
                        modifier = Modifier.testTag("share_job_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = tokens.textSecondary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleSave() },
                        modifier = Modifier.testTag("details_save_job_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = if (uiState.isSaved) "Remove from saved" else "Save job",
                            tint = if (uiState.isSaved) tokens.primary else tokens.textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = tokens.surface)
            )
        },
        bottomBar = {
            JobDetailsBottomBar(
                job = job,
                isSaved = uiState.isSaved,
                onSaveToggle = { viewModel.toggleSave() },
                onApplyClick = {
                    val url = job.applicationUrl ?: job.applicationEmail?.let { "mailto:$it" }
                    if (!url.isNullOrBlank()) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Unable to open application link", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "No external application link specified for this role", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(tokens.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card: Title, Company, Badges
            Card(
                colors = CardDefaults.cardColors(containerColor = tokens.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (job.isUrgent) {
                            Badge(
                                text = "Urgent Hiring",
                                color = tokens.error,
                                backgroundColor = tokens.error.copy(alpha = 0.1f),
                                icon = Icons.Default.FlashOn
                            )
                        }
                        if (job.isFeatured) {
                            Badge(
                                text = "Featured",
                                color = tokens.warning,
                                backgroundColor = tokens.warning.copy(alpha = 0.12f),
                                icon = Icons.Default.Star
                            )
                        }
                        Badge(
                            text = job.categoryName,
                            color = tokens.primary,
                            backgroundColor = tokens.primaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = tokens.primary
                        )
                        Text(
                            text = job.companyName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = tokens.textSecondary
                        )
                        Text(
                            text = "${job.location}, ${job.district}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textSecondary
                        )
                    }
                }
            }

            // Key Attributes Grid
            Card(
                colors = CardDefaults.cardColors(containerColor = tokens.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow(
                        icon = Icons.Default.Payments,
                        label = "Monthly Salary",
                        value = job.formattedSalary
                    )
                    DetailRow(
                        icon = Icons.Default.Work,
                        label = "Job Type",
                        value = "${job.employmentType.label} • ${job.workplaceType.label}"
                    )
                    DetailRow(
                        icon = Icons.Default.CheckCircle,
                        label = "Experience Level",
                        value = job.experienceLevel.label
                    )
                    DetailRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Posted Date",
                        value = job.postedDate + (if (!job.closingDate.isNullOrBlank()) " (Closes: ${job.closingDate})" else "")
                    )
                }
            }

            // Description Section
            DetailSectionCard(title = "Job Description") {
                Text(
                    text = job.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.textSecondary,
                    lineHeight = 22.sp
                )
            }

            // Responsibilities Section
            if (job.responsibilities.isNotEmpty()) {
                DetailSectionCard(title = "Key Responsibilities") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        job.responsibilities.forEach { item ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp),
                                    tint = tokens.primary
                                )
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.textSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Requirements Section
            if (job.requirements.isNotEmpty()) {
                DetailSectionCard(title = "Requirements & Qualifications") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        job.requirements.forEach { item ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .padding(top = 6.dp)
                                        .background(tokens.secondary, CircleShape)
                                )
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.textSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Benefits Section
            if (job.benefits.isNotEmpty()) {
                DetailSectionCard(title = "Benefits & Perks") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        job.benefits.forEach { benefit ->
                            Surface(
                                color = tokens.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "✓ $benefit",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = tokens.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Employer Transparency Card
            Card(
                colors = CardDefaults.cardColors(containerColor = tokens.surfaceElevated),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified",
                        modifier = Modifier.size(24.dp),
                        tint = tokens.success
                    )
                    Column {
                        Text(
                            text = "Verified Listing Source: ${job.source}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.textPrimary
                        )
                        Text(
                            text = "LankaJobs connects you directly to the official employer portal.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    val tokens = LankaJobsTheme.tokens
    Card(
        colors = CardDefaults.cardColors(containerColor = tokens.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    val tokens = LankaJobsTheme.tokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(tokens.background, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = tokens.primary
            )
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = tokens.textPrimary
            )
        }
    }
}

@Composable
private fun JobDetailsBottomBar(
    job: Job,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onApplyClick: () -> Unit
) {
    val tokens = LankaJobsTheme.tokens

    Surface(
        color = tokens.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedIconButton(
                onClick = onSaveToggle,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("bottom_bar_save_button"),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSaved) tokens.primary else tokens.border
                )
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (isSaved) "Remove from saved" else "Save job",
                    tint = if (isSaved) tokens.primary else tokens.textSecondary
                )
            }

            Button(
                onClick = onApplyClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("apply_now_button"),
                colors = ButtonDefaults.buttonColors(containerColor = tokens.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Apply Now",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
