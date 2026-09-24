package com.example.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.Advertisement
import com.example.domain.model.Job
import com.example.domain.model.JobCategory
import com.example.presentation.components.AdvertisementCard
import com.example.presentation.components.JobCard
import com.example.presentation.components.LoadingState
import com.example.ui.theme.LankaJobsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onJobClick: (String) -> Unit,
    onCategoryClick: (String, String) -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAdClick: (String) -> Unit,
    onBrowseAdsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = LankaJobsTheme.tokens

    if (uiState.isLoading) {
        LoadingState(modifier = modifier.fillMaxSize())
        return
    }

    val handleJobClick = androidx.compose.runtime.remember(onJobClick) {
        { job: Job -> onJobClick(job.id) }
    }
    val handleSaveToggle = androidx.compose.runtime.remember(viewModel) {
        { job: Job -> viewModel.toggleSave(job) }
    }
    val handleAdClick = androidx.compose.runtime.remember(onAdClick) {
        { ad: Advertisement -> onAdClick(ad.id) }
    }

    val categoriesRowState = rememberLazyListState()
    val suggestedAdsRowState = rememberLazyListState()
    val featuredJobsRowState = rememberLazyListState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.background)
            .testTag("home_screen_feed"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Top Header Bar
        item(key = "header_section", contentType = "header") {
            HomeHeaderSection(
                userName = uiState.userProfile?.fullName?.split(" ")?.firstOrNull(),
                onNotificationsClick = onNotificationsClick,
                onBrowseAdsClick = onBrowseAdsClick
            )
        }

        // 2. Search Launcher Bar
        item(key = "search_launcher_section", contentType = "search") {
            SearchLauncherBar(
                onClick = onSearchClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // 3. Categories Horizontal Section
        if (uiState.categories.isNotEmpty()) {
            item(key = "categories_section", contentType = "categories") {
                SectionHeader(
                    title = "Popular Categories",
                    actionLabel = "View All",
                    onActionClick = onSearchClick,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )

                LazyRow(
                    state = categoriesRowState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.categories, key = { it.id }, contentType = { "category" }) { category ->
                        CategoryPillCard(
                            category = category,
                            onClick = { onCategoryClick(category.id, category.name) }
                        )
                    }
                }
            }
        }

        // 4. Personalized Suggestions Section (Tailored to user's location & categories)
        if (uiState.suggestedAds.isNotEmpty() && uiState.userProfile != null) {
            item(key = "suggested_ads_section", contentType = "suggested_ads") {
                val loc = uiState.userProfile?.preferredLocation ?: "Sri Lanka"
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = tokens.primary,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "FOR YOU",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "Recommended Announcements",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Based on your preferences in $loc",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.textSecondary
                            )
                        }

                        TextButton(
                            onClick = onBrowseAdsClick,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "View All (${uiState.suggestedAds.size})",
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        state = suggestedAdsRowState,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.suggestedAds, key = { "sug_ad_${it.id}" }, contentType = { "ad" }) { ad ->
                            Box(modifier = Modifier.width(300.dp)) {
                                AdvertisementCard(
                                    ad = ad,
                                    onAdClick = handleAdClick
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Featured Jobs Carousel / Section
        if (uiState.featuredJobs.isNotEmpty()) {
            item(key = "featured_section", contentType = "featured") {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Featured Opportunities",
                    subtitle = "Handpicked verified roles across Sri Lanka",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                LazyRow(
                    state = featuredJobsRowState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.featuredJobs, key = { "feat_${it.id}" }, contentType = { "featured_job" }) { job ->
                        Box(modifier = Modifier.width(310.dp)) {
                            JobCard(
                                job = job,
                                isSaved = uiState.savedJobIds.contains(job.id),
                                onJobClick = handleJobClick,
                                onSaveToggle = handleSaveToggle
                            )
                        }
                    }
                }
            }
        }

        // 5. Sponsored / Advertisement Banner (if available)
        val highlightedAd = uiState.sponsoredAds.firstOrNull()
        if (highlightedAd != null) {
            item(key = "sponsored_ad_banner", contentType = "sponsored") {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Promoted Announcements",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.textPrimary
                        )
                        TextButton(
                            onClick = onBrowseAdsClick,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "All Announcements",
                                style = MaterialTheme.typography.labelMedium,
                                color = tokens.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    AdvertisementCard(
                        ad = highlightedAd,
                        onAdClick = handleAdClick
                    )
                }
            }
        }

        // 6. Latest Jobs Feed Header
        item(key = "latest_jobs_header", contentType = "latest_header") {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Latest Job Openings",
                subtitle = "Recent postings in Sri Lanka",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // 7. Latest Jobs Vertical Feed
        items(uiState.latestJobs, key = { it.id }, contentType = { "job_card" }) { job ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                JobCard(
                    job = job,
                    isSaved = uiState.savedJobIds.contains(job.id),
                    onJobClick = handleJobClick,
                    onSaveToggle = handleSaveToggle
                )
            }
        }
    }
}

@Composable
private fun HomeHeaderSection(
    userName: String?,
    onNotificationsClick: () -> Unit,
    onBrowseAdsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LankaJobsTheme.tokens

    Surface(
        color = tokens.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(tokens.primary, CircleShape)
                    )
                    Text(
                        text = "LankaJobs",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = tokens.primary,
                        fontSize = 22.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (!userName.isNullOrBlank()) "Hi, $userName 👋" else "Discover career opportunities in Sri Lanka",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onBrowseAdsClick,
                    modifier = Modifier.testTag("home_ads_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Browse Ads",
                        tint = tokens.textSecondary
                    )
                }
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("home_notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = tokens.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchLauncherBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LankaJobsTheme.tokens

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("home_search_launcher"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = tokens.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = tokens.primary,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = "Search jobs, companies or locations...",
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.textMuted
            )
        }
    }
}

@Composable
private fun CategoryPillCard(
    category: JobCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LankaJobsTheme.tokens

    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("category_pill_${category.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = tokens.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, tokens.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(tokens.primaryContainer, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = tokens.primary
                )
            }

            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.textPrimary
                )
                Text(
                    text = "${category.jobCount} jobs",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LankaJobsTheme.tokens

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textSecondary
                )
            }
        }

        if (actionLabel != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
