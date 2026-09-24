package com.example.presentation.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.components.AdvertisementCard
import com.example.presentation.components.EmptyState
import com.example.presentation.components.LankaJobsTopBar
import com.example.presentation.components.LoadingState
import com.example.ui.theme.LankaJobsTheme

@Composable
fun AdsListScreen(
    viewModel: AdsViewModel,
    onAdClick: (String) -> Unit,
    onCreateAdClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = LankaJobsTheme.tokens
    var selectedTab by remember { mutableIntStateOf(0) }

    val userProfile = uiState.userProfile
    val userLocation = userProfile?.preferredLocation ?: "Sri Lanka"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LankaJobsTopBar(
                title = "Promotions & Courses",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateAdClick,
                containerColor = tokens.primary,
                contentColor = tokens.surface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("create_ad_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Post Advertisement")
                    Text("Post Ad", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(tokens.background)
        ) {
            Surface(
                color = tokens.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Verified Career & Training Announcements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = tokens.textPrimary
                    )
                    Text(
                        text = "Suggestions tailored to your district ($userLocation) and selected career fields.",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.textSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = tokens.surface,
                        contentColor = tokens.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = tokens.primary
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    text = "Suggested For You (${uiState.suggestedAds.size})",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("tab_suggested_ads")
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Text(
                                    text = "All Listings (${uiState.approvedAds.size})",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("tab_all_ads")
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                LoadingState(modifier = Modifier.weight(1f))
                return@Scaffold
            }

            val displayedAds = if (selectedTab == 0) uiState.suggestedAds else uiState.approvedAds

            if (displayedAds.isEmpty()) {
                EmptyState(
                    title = if (selectedTab == 0) "No Specific Suggestions" else "No Announcements Yet",
                    subtitle = if (selectedTab == 0)
                        "There are currently no active announcements matching $userLocation. Check the 'All Listings' tab or adjust your profile location."
                    else
                        "Be the first to post a professional advertisement, training course, or walk-in interview on LankaJobs.",
                    actionLabel = if (selectedTab == 0) "View All Announcements" else "Post Advertisement",
                    onActionClick = {
                        if (selectedTab == 0) selectedTab = 1 else onCreateAdClick()
                    },
                    icon = Icons.Default.Campaign,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("ads_list_feed"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedAds, key = { it.id }) { ad ->
                        AdvertisementCard(
                            ad = ad,
                            onAdClick = { onAdClick(it.id) }
                        )
                    }
                }
            }
        }
    }
}
