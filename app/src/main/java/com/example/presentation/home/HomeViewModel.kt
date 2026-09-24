package com.example.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Advertisement
import com.example.domain.model.Job
import com.example.domain.model.JobCategory
import com.example.domain.model.UserProfile
import com.example.domain.repository.AdvertisementRepository
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.JobRepository
import com.example.domain.repository.SavedJobRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val categories: List<JobCategory> = emptyList(),
    val suggestedAds: List<Advertisement> = emptyList(),
    val suggestedJobs: List<Job> = emptyList(),
    val featuredJobs: List<Job> = emptyList(),
    val latestJobs: List<Job> = emptyList(),
    val sponsoredAds: List<Advertisement> = emptyList(),
    val savedJobIds: Set<String> = emptySet(),
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val jobRepository: JobRepository,
    private val savedJobRepository: SavedJobRepository,
    private val advertisementRepository: AdvertisementRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val jobsOverviewFlow = combine(
        jobRepository.getCategories(),
        jobRepository.getFeaturedJobs(),
        jobRepository.getLatestJobs(15)
    ) { categories, featured, latest ->
        Triple(categories, featured, latest)
    }

    // Personalized suggestions based on user profile preferences
    private val suggestionsFlow = authRepository.currentUser.flatMapLatest { user ->
        val cats = user?.preferredCategories ?: emptyList()
        val loc = user?.preferredLocation ?: ""
        combine(
            advertisementRepository.getSuggestedAdvertisements(cats, loc),
            jobRepository.getSuggestedJobs(cats, loc)
        ) { ads, jobs ->
            Pair(ads, jobs)
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.currentUser,
        jobsOverviewFlow,
        suggestionsFlow,
        advertisementRepository.getApprovedAdvertisements(),
        savedJobRepository.getSavedJobIds()
    ) { user, (categories, featured, latest), (suggestedAds, suggestedJobs), allAds, savedIds ->
        HomeUiState(
            isLoading = false,
            userProfile = user,
            categories = categories,
            suggestedAds = suggestedAds,
            suggestedJobs = suggestedJobs,
            featuredJobs = featured,
            latestJobs = latest,
            sponsoredAds = allAds,
            savedJobIds = savedIds,
            errorMessage = null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun toggleSave(job: Job) {
        viewModelScope.launch {
            savedJobRepository.toggleSaveJob(job.id)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            jobRepository.refreshJobs()
            _isRefreshing.value = false
        }
    }
}
