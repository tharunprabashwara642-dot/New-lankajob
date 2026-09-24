package com.example.presentation.saved

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.components.EmptyState
import com.example.presentation.components.JobCard
import com.example.presentation.components.LoadingState
import com.example.ui.theme.LankaJobsTheme

@Composable
fun SavedJobsScreen(
    viewModel: SavedJobsViewModel,
    onJobClick: (String) -> Unit,
    onExploreJobsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = LankaJobsTheme.tokens

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.background)
    ) {
        // Top Header
        Surface(
            color = tokens.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Saved Jobs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = tokens.textPrimary
                )
                Text(
                    text = "Your bookmarked listings are saved offline for easy access",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.textSecondary
                )

                if (uiState.savedJobs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = {
                            Text(
                                text = "Search saved jobs...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = tokens.textMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = tokens.textMuted
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = tokens.textMuted
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("saved_search_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = tokens.surface,
                            unfocusedContainerColor = tokens.surface,
                            focusedBorderColor = tokens.primary,
                            unfocusedBorderColor = tokens.border
                        ),
                        singleLine = true
                    )
                }
            }
        }

        if (uiState.isLoading) {
            LoadingState(modifier = Modifier.weight(1f))
            return
        }

        if (uiState.savedJobs.isEmpty()) {
            EmptyState(
                title = "No Saved Jobs Yet",
                subtitle = "Jobs you save will appear here. Bookmark opportunities you're interested in applying for later.",
                actionLabel = "Explore Jobs",
                onActionClick = onExploreJobsClick,
                icon = Icons.Default.BookmarkBorder,
                modifier = Modifier.weight(1f)
            )
        } else if (uiState.filteredJobs.isEmpty()) {
            EmptyState(
                title = "No Matches Found",
                subtitle = "None of your saved jobs match \"${uiState.searchQuery}\"",
                actionLabel = "Clear Search",
                onActionClick = { viewModel.onSearchQueryChange("") },
                modifier = Modifier.weight(1f)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.filteredJobs.size} saved jobs",
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.textSecondary
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("saved_jobs_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.filteredJobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        isSaved = true,
                        onJobClick = { onJobClick(it.id) },
                        onSaveToggle = { viewModel.removeSavedJob(it.id) }
                    )
                }
            }
        }
    }
}
