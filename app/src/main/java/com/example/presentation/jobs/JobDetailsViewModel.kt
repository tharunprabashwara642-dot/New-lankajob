package com.example.presentation.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Job
import com.example.domain.repository.JobRepository
import com.example.domain.repository.SavedJobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class JobDetailsUiState(
    val isLoading: Boolean = true,
    val job: Job? = null,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class JobDetailsViewModel(
    private val jobId: String,
    private val jobRepository: JobRepository,
    private val savedJobRepository: SavedJobRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<JobDetailsUiState> = combine(
        jobRepository.getJobById(jobId),
        savedJobRepository.getSavedJobIds(),
        _errorMessage
    ) { job, savedIds, error ->
        JobDetailsUiState(
            isLoading = false,
            job = job,
            isSaved = job != null && savedIds.contains(job.id),
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = JobDetailsUiState(isLoading = true)
    )

    fun toggleSave() {
        val currentJob = uiState.value.job ?: return
        viewModelScope.launch {
            savedJobRepository.toggleSaveJob(currentJob.id)
        }
    }
}
