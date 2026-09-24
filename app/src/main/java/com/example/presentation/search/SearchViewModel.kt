package com.example.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.EmploymentType
import com.example.domain.model.ExperienceLevel
import com.example.domain.model.Job
import com.example.domain.model.JobCategory
import com.example.domain.model.JobFilter
import com.example.domain.model.JobSortOrder
import com.example.domain.model.WorkplaceType
import com.example.domain.repository.JobRepository
import com.example.domain.repository.SavedJobRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchUiState(
    val filter: JobFilter = JobFilter(),
    val results: List<Job> = emptyList(),
    val categories: List<JobCategory> = emptyList(),
    val locations: List<String> = emptyList(),
    val savedJobIds: Set<String> = emptySet(),
    val isFilterSheetVisible: Boolean = false,
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val jobRepository: JobRepository,
    private val savedJobRepository: SavedJobRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(JobFilter())
    val filter: StateFlow<JobFilter> = _filter.asStateFlow()

    private val _isFilterSheetVisible = MutableStateFlow(false)
    val isFilterSheetVisible: StateFlow<Boolean> = _isFilterSheetVisible.asStateFlow()

    private val _categories = MutableStateFlow<List<JobCategory>>(emptyList())
    private val _locations = MutableStateFlow<List<String>>(emptyList())

    init {
        viewModelScope.launch {
            jobRepository.getCategories().collect {
                _categories.value = it
            }
        }
        viewModelScope.launch {
            _locations.value = jobRepository.getAvailableLocations()
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val searchResultsFlow = combine(
        _filter.debounce { if (it.query.isBlank()) 0L else 200L }.flatMapLatest { jobRepository.getJobs(it) },
        savedJobRepository.getSavedJobIds()
    ) { jobs, savedIds ->
        Pair(jobs, savedIds)
    }

    val uiState: StateFlow<SearchUiState> = combine(
        _filter,
        searchResultsFlow,
        _categories,
        _locations,
        _isFilterSheetVisible
    ) { filter, (jobs, savedIds), categories, locations, sheetVisible ->
        SearchUiState(
            filter = filter,
            results = jobs,
            categories = categories,
            locations = locations,
            savedJobIds = savedIds,
            isFilterSheetVisible = sheetVisible,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState(isLoading = true)
    )

    fun onQueryChange(newQuery: String) {
        _filter.value = _filter.value.copy(query = newQuery)
    }

    fun onCategorySelect(categoryId: String?) {
        val current = _filter.value.categoryId
        val next = if (current == categoryId) null else categoryId
        _filter.value = _filter.value.copy(categoryId = next)
    }

    fun onLocationSelect(location: String?) {
        val current = _filter.value.location
        val next = if (current == location) null else location
        _filter.value = _filter.value.copy(location = next)
    }

    fun onEmploymentTypeSelect(type: EmploymentType?) {
        val current = _filter.value.employmentType
        val next = if (current == type) null else type
        _filter.value = _filter.value.copy(employmentType = next)
    }

    fun onWorkplaceTypeSelect(type: WorkplaceType?) {
        val current = _filter.value.workplaceType
        val next = if (current == type) null else type
        _filter.value = _filter.value.copy(workplaceType = next)
    }

    fun onExperienceLevelSelect(level: ExperienceLevel?) {
        val current = _filter.value.experienceLevel
        val next = if (current == level) null else level
        _filter.value = _filter.value.copy(experienceLevel = next)
    }

    fun onSortOrderSelect(sortOrder: JobSortOrder) {
        _filter.value = _filter.value.copy(sortOrder = sortOrder)
    }

    fun onSalaryChange(minSalary: Int?) {
        _filter.value = _filter.value.copy(minSalary = minSalary)
    }

    fun toggleFeaturedOnly() {
        _filter.value = _filter.value.copy(onlyFeatured = !_filter.value.onlyFeatured)
    }

    fun resetFilters() {
        _filter.value = JobFilter(query = _filter.value.query)
    }

    fun setFilterSheetVisible(visible: Boolean) {
        _isFilterSheetVisible.value = visible
    }

    fun toggleSave(job: Job) {
        viewModelScope.launch {
            savedJobRepository.toggleSaveJob(job.id)
        }
    }
}
