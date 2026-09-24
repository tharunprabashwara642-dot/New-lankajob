package com.example.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.domain.model.EmploymentType
import com.example.domain.model.ExperienceLevel
import com.example.domain.model.JobSortOrder
import com.example.domain.model.WorkplaceType
import com.example.presentation.components.EmptyState
import com.example.presentation.components.JobCard
import com.example.ui.theme.LankaJobsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onJobClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tokens = LankaJobsTheme.tokens

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.background)
    ) {
        // Search Input Header
        Surface(
            color = tokens.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.filter.query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    placeholder = {
                        Text(
                            text = "Job title, company, skills, or district...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.textMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = tokens.primary
                        )
                    },
                    trailingIcon = {
                        if (uiState.filter.query.isNotBlank()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
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
                        .testTag("search_text_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = tokens.surface,
                        unfocusedContainerColor = tokens.surface,
                        focusedBorderColor = tokens.primary,
                        unfocusedBorderColor = tokens.border
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Filter Sheet Trigger Button
                    Surface(
                        onClick = { viewModel.setFilterSheetVisible(true) },
                        color = if (uiState.filter.activeFilterCount > 0) tokens.primaryContainer else tokens.surface,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (uiState.filter.activeFilterCount > 0) tokens.primary else tokens.border
                        ),
                        modifier = Modifier.testTag("open_filters_sheet_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filters",
                                modifier = Modifier.size(16.dp),
                                tint = if (uiState.filter.activeFilterCount > 0) tokens.primary else tokens.textSecondary
                            )
                            Text(
                                text = if (uiState.filter.activeFilterCount > 0) "Filters (${uiState.filter.activeFilterCount})" else "Filters",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (uiState.filter.activeFilterCount > 0) tokens.primary else tokens.textSecondary
                            )
                        }
                    }

                    // Quick Remote Filter
                    FilterChip(
                        selected = uiState.filter.workplaceType == WorkplaceType.REMOTE,
                        onClick = { viewModel.onWorkplaceTypeSelect(WorkplaceType.REMOTE) },
                        label = { Text("Remote Only", style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tokens.primaryContainer,
                            selectedLabelColor = tokens.primary
                        )
                    )

                    // Quick Full-time Filter
                    FilterChip(
                        selected = uiState.filter.employmentType == EmploymentType.FULL_TIME,
                        onClick = { viewModel.onEmploymentTypeSelect(EmploymentType.FULL_TIME) },
                        label = { Text("Full-time", style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tokens.primaryContainer,
                            selectedLabelColor = tokens.primary
                        )
                    )

                    // Quick Colombo Filter
                    FilterChip(
                        selected = uiState.filter.location == "Colombo",
                        onClick = { viewModel.onLocationSelect("Colombo") },
                        label = { Text("Colombo", style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tokens.primaryContainer,
                            selectedLabelColor = tokens.primary
                        )
                    )

                    if (uiState.filter.activeFilterCount > 0) {
                        TextButton(
                            onClick = { viewModel.resetFilters() },
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(
                                text = "Reset",
                                style = MaterialTheme.typography.labelSmall,
                                color = tokens.error
                            )
                        }
                    }
                }
            }
        }

        // Results Summary Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.results.size} jobs found",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = tokens.textPrimary
            )

            Text(
                text = "Sorted by: ${uiState.filter.sortOrder.label}",
                style = MaterialTheme.typography.bodySmall,
                color = tokens.textSecondary
            )
        }

        // Job Results List or Empty State
        if (uiState.results.isEmpty()) {
            EmptyState(
                title = "No jobs match your search",
                subtitle = "Try adjusting your keywords, locations, or clear filters to view all available listings.",
                actionLabel = "Clear All Filters",
                onActionClick = { viewModel.resetFilters() },
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("search_results_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.results, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        isSaved = uiState.savedJobIds.contains(job.id),
                        onJobClick = { onJobClick(it.id) },
                        onSaveToggle = { viewModel.toggleSave(it) }
                    )
                }
            }
        }
    }

    // Comprehensive Filter Modal Bottom Sheet
    if (uiState.isFilterSheetVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.setFilterSheetVisible(false) },
            sheetState = sheetState,
            containerColor = tokens.surface
        ) {
            FilterBottomSheetContent(
                uiState = uiState,
                onSortSelect = { viewModel.onSortOrderSelect(it) },
                onCategorySelect = { viewModel.onCategorySelect(it) },
                onLocationSelect = { viewModel.onLocationSelect(it) },
                onEmploymentSelect = { viewModel.onEmploymentTypeSelect(it) },
                onWorkplaceSelect = { viewModel.onWorkplaceTypeSelect(it) },
                onExperienceSelect = { viewModel.onExperienceLevelSelect(it) },
                onSalarySelect = { viewModel.onSalaryChange(it) },
                onToggleFeatured = { viewModel.toggleFeaturedOnly() },
                onReset = { viewModel.resetFilters() },
                onApply = { viewModel.setFilterSheetVisible(false) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterBottomSheetContent(
    uiState: SearchUiState,
    onSortSelect: (JobSortOrder) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onLocationSelect: (String?) -> Unit,
    onEmploymentSelect: (EmploymentType?) -> Unit,
    onWorkplaceSelect: (WorkplaceType?) -> Unit,
    onExperienceSelect: (ExperienceLevel?) -> Unit,
    onSalarySelect: (Int?) -> Unit,
    onToggleFeatured: () -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    val tokens = LankaJobsTheme.tokens

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Sheet Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter Jobs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = tokens.textPrimary
            )
            TextButton(onClick = onReset) {
                Text(
                    text = "Reset All",
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.error
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sort By Section
        FilterSectionTitle(title = "Sort Order")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            JobSortOrder.entries.forEach { sort ->
                FilterChip(
                    selected = uiState.filter.sortOrder == sort,
                    onClick = { onSortSelect(sort) },
                    label = { Text(sort.label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tokens.primaryContainer,
                        selectedLabelColor = tokens.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Workplace Type (Remote / Hybrid / On-site)
        FilterSectionTitle(title = "Workplace Environment")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WorkplaceType.entries.forEach { type ->
                FilterChip(
                    selected = uiState.filter.workplaceType == type,
                    onClick = { onWorkplaceSelect(type) },
                    label = { Text(type.label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tokens.primaryContainer,
                        selectedLabelColor = tokens.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Locations in Sri Lanka
        FilterSectionTitle(title = "Location / District")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.locations.forEach { loc ->
                FilterChip(
                    selected = uiState.filter.location == loc,
                    onClick = { onLocationSelect(loc) },
                    label = { Text(loc, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tokens.primaryContainer,
                        selectedLabelColor = tokens.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Employment Type
        FilterSectionTitle(title = "Employment Type")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EmploymentType.entries.forEach { type ->
                FilterChip(
                    selected = uiState.filter.employmentType == type,
                    onClick = { onEmploymentSelect(type) },
                    label = { Text(type.label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tokens.primaryContainer,
                        selectedLabelColor = tokens.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Experience Level
        FilterSectionTitle(title = "Experience Level")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExperienceLevel.entries.forEach { level ->
                FilterChip(
                    selected = uiState.filter.experienceLevel == level,
                    onClick = { onExperienceSelect(level) },
                    label = { Text(level.label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tokens.primaryContainer,
                        selectedLabelColor = tokens.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Minimum Monthly Salary
        FilterSectionTitle(title = "Minimum Salary (LKR / month)")
        val salaryThresholds = listOf(
            50000 to "50,000+",
            100000 to "100,000+",
            200000 to "200,000+",
            300000 to "300,000+"
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            salaryThresholds.forEach { (amount, label) ->
                FilterChip(
                    selected = uiState.filter.minSalary == amount,
                    onClick = {
                        val next = if (uiState.filter.minSalary == amount) null else amount
                        onSalarySelect(next)
                    },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tokens.primaryContainer,
                        selectedLabelColor = tokens.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Apply Button
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("apply_filters_button"),
            colors = ButtonDefaults.buttonColors(containerColor = tokens.primary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Show Results (${uiState.results.size})",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FilterSectionTitle(title: String) {
    val tokens = LankaJobsTheme.tokens
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = tokens.textPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
