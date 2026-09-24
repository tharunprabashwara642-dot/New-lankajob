package com.example.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.NotificationPreference
import com.example.domain.repository.NotificationRepository
import com.example.presentation.components.LankaJobsTopBar
import com.example.ui.theme.LankaJobsTheme
import kotlinx.coroutines.launch

@Composable
fun NotificationSettingsScreen(
    notificationRepository: NotificationRepository,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prefs by notificationRepository.preferences.collectAsState(initial = NotificationPreference())
    val tokens = LankaJobsTheme.tokens
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LankaJobsTopBar(
                title = "Notification Preferences",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(tokens.background)
                .padding(16.dp),
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
                        text = "Alerts & Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = "Customize the alerts you want to receive about careers and approved sponsored announcements.",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    NotificationToggleItem(
                        title = "New Job Alerts",
                        subtitle = "Notify me when fresh jobs are posted matching my preferences",
                        checked = prefs.newJobs,
                        onCheckedChange = {
                            scope.launch {
                                notificationRepository.updatePreferences(prefs.copy(newJobs = it))
                            }
                        },
                        testTag = "toggle_new_jobs"
                    )

                    NotificationDivider()

                    NotificationToggleItem(
                        title = "Featured & Urgent Openings",
                        subtitle = "Receive priority alerts for urgent openings and top verified employers",
                        checked = prefs.featuredJobs,
                        onCheckedChange = {
                            scope.launch {
                                notificationRepository.updatePreferences(prefs.copy(featuredJobs = it))
                            }
                        },
                        testTag = "toggle_featured_jobs"
                    )

                    NotificationDivider()

                    NotificationToggleItem(
                        title = "Approved Advertisements & Fairs",
                        subtitle = "Get notified about verified career fairs and educational diploma programs",
                        checked = prefs.advertisements,
                        onCheckedChange = {
                            scope.launch {
                                notificationRepository.updatePreferences(prefs.copy(advertisements = it))
                            }
                        },
                        testTag = "toggle_advertisements"
                    )

                    NotificationDivider()

                    NotificationToggleItem(
                        title = "Saved Job Updates",
                        subtitle = "Alert me when a bookmarked job is nearing its closing deadline",
                        checked = prefs.savedJobUpdates,
                        onCheckedChange = {
                            scope.launch {
                                notificationRepository.updatePreferences(prefs.copy(savedJobUpdates = it))
                            }
                        },
                        testTag = "toggle_saved_job_updates"
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val tokens = LankaJobsTheme.tokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = tokens.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = tokens.surface,
                checkedTrackColor = tokens.primary
            )
        )
    }
}

@Composable
private fun NotificationDivider() {
    val tokens = LankaJobsTheme.tokens
    androidx.compose.material3.HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 1.dp,
        color = tokens.border
    )
}
