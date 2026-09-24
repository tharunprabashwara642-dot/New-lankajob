package com.example.presentation.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Job
import com.example.domain.repository.SavedJobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SavedJobsUiState(
    val isLoading: Boolean = true,
    val savedJobs: List<Job> = emptyList(),
    val filteredJobs: List<Job> = emptyList(),
    val searchQuery: String = ""
)

class SavedJobsViewModel(
    private val savedJobRepository: SavedJobRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<SavedJobsUiState> = combine(
        savedJobRepository.getSavedJobs(),
        _searchQuery
    ) { savedJobs, query ->
        val filtered = if (query.isBlank()) {
            savedJobs
        } else {
            val q = query.trim().lowercase()
            savedJobs.filter {
                it.title.lowercase().contains(q) ||
                it.companyName.lowercase().contains(q) ||
                it.location.lowercase().contains(q)
            }
        }
        SavedJobsUiState(
            isLoading = false,
            savedJobs = savedJobs,
            filteredJobs = filtered,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SavedJobsUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun removeSavedJob(jobId: String) {
        viewModelScope.launch {
            savedJobRepository.removeSavedJob(jobId)
        }
    }
}
