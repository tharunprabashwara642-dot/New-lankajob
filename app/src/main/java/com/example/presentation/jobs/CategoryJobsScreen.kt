package com.example.presentation.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.Job
import com.example.domain.repository.JobRepository
import com.example.domain.repository.SavedJobRepository
import com.example.presentation.components.EmptyState
import com.example.presentation.components.JobCard
import com.example.presentation.components.LankaJobsTopBar
import com.example.ui.theme.LankaJobsTheme
import kotlinx.coroutines.launch

@Composable
fun CategoryJobsScreen(
    categoryId: String,
    categoryName: String,
    jobRepository: JobRepository,
    savedJobRepository: SavedJobRepository,
    onJobClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val jobs by jobRepository.getJobsByCategory(categoryId).collectAsState(initial = emptyList())
    val savedIds by savedJobRepository.getSavedJobIds().collectAsState(initial = emptySet())
    val tokens = LankaJobsTheme.tokens
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LankaJobsTopBar(
                title = categoryName,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(tokens.background)
        ) {
            Text(
                text = "${jobs.size} open positions in $categoryName",
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )

            if (jobs.isEmpty()) {
                EmptyState(
                    title = "No Openings in $categoryName",
                    subtitle = "There are currently no active job vacancies listed under this category.",
                    actionLabel = "Back to Categories",
                    onActionClick = onBackClick,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("category_jobs_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(jobs, key = { it.id }) { job ->
                        JobCard(
                            job = job,
                            isSaved = savedIds.contains(job.id),
                            onJobClick = { onJobClick(it.id) },
                            onSaveToggle = {
                                scope.launch {
                                    savedJobRepository.toggleSaveJob(it.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
