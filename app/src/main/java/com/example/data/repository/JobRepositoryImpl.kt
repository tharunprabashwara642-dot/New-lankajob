package com.example.data.repository

import com.example.data.local.LankaJobsDatabase
import com.example.data.local.SeedData
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Job
import com.example.domain.model.JobCategory
import com.example.domain.model.JobFilter
import com.example.domain.model.JobSortOrder
import com.example.domain.repository.JobRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class JobRepositoryImpl(
    private val database: LankaJobsDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : JobRepository {

    private val jobDao = database.jobDao()

    override fun getJobs(filter: JobFilter): Flow<List<Job>> {
        return jobDao.getAllJobs().map { entities ->
            var list = entities.map { it.toDomain() }

            // Filter by Query (title, company, location, category)
            if (filter.query.isNotBlank()) {
                val q = filter.query.trim().lowercase()
                list = list.filter {
                    it.title.lowercase().contains(q) ||
                    it.companyName.lowercase().contains(q) ||
                    it.location.lowercase().contains(q) ||
                    it.categoryName.lowercase().contains(q) ||
                    it.description.lowercase().contains(q)
                }
            }

            // Filter by Category
            if (!filter.categoryId.isNullOrBlank()) {
                list = list.filter { it.categoryId.equals(filter.categoryId, ignoreCase = true) }
            }

            // Filter by Location
            if (!filter.location.isNullOrBlank()) {
                list = list.filter {
                    it.location.contains(filter.location, ignoreCase = true) ||
                    it.district.contains(filter.location, ignoreCase = true)
                }
            }

            // Filter by Employment Type
            if (filter.employmentType != null) {
                list = list.filter { it.employmentType == filter.employmentType }
            }

            // Filter by Experience Level
            if (filter.experienceLevel != null) {
                list = list.filter { it.experienceLevel == filter.experienceLevel }
            }

            // Filter by Workplace Type (Remote, Hybrid, On-site)
            if (filter.workplaceType != null) {
                list = list.filter { it.workplaceType == filter.workplaceType }
            }

            // Filter by Minimum Salary
            if (filter.minSalary != null && filter.minSalary > 0) {
                list = list.filter {
                    val max = it.salaryMax ?: it.salaryMin ?: 0
                    max >= filter.minSalary
                }
            }

            // Filter by Featured / Urgent
            if (filter.onlyFeatured) {
                list = list.filter { it.isFeatured }
            }
            if (filter.onlyUrgent) {
                list = list.filter { it.isUrgent }
            }

            // Sorting
            when (filter.sortOrder) {
                JobSortOrder.NEWEST -> {
                    list = list.sortedWith(
                        compareByDescending<Job> { it.isFeatured }
                            .thenByDescending { it.id }
                    )
                }
                JobSortOrder.SALARY_HIGH_TO_LOW -> {
                    list = list.sortedByDescending { it.salaryMax ?: it.salaryMin ?: 0 }
                }
                JobSortOrder.RELEVANCE -> {
                    list = list.sortedWith(
                        compareByDescending<Job> { it.isFeatured }
                            .thenByDescending { it.isUrgent }
                    )
                }
            }

            list
        }.flowOn(ioDispatcher)
    }

    override fun getSuggestedJobs(
        preferredCategories: List<String>,
        preferredLocation: String
    ): Flow<List<Job>> {
        return jobDao.getAllJobs().map { entities ->
            val allJobs = entities.map { it.toDomain() }
            if (preferredCategories.isEmpty() && preferredLocation.isBlank()) {
                allJobs.take(10)
            } else {
                val normalizedLoc = preferredLocation.trim().lowercase()
                val normalizedCats = preferredCategories.map { it.trim().lowercase() }

                allJobs.map { job ->
                    var score = 0
                    val jobCat = job.categoryName.lowercase()
                    val jobLoc = job.location.lowercase()
                    val jobDist = job.district.lowercase()

                    if (normalizedCats.any { jobCat.contains(it) || it.contains(jobCat) }) {
                        score += 3
                    }
                    if (normalizedLoc.isNotBlank() && (jobLoc.contains(normalizedLoc) || jobDist.contains(normalizedLoc))) {
                        score += 2
                    }
                    if (job.workplaceType == com.example.domain.model.WorkplaceType.REMOTE) {
                        score += 1
                    }
                    Pair(job, score)
                }
                    .filter { it.second > 0 }
                    .sortedWith(compareByDescending<Pair<Job, Int>> { it.second }.thenByDescending { it.first.isFeatured })
                    .map { it.first }
                    .ifEmpty { allJobs.take(6) }
            }
        }.flowOn(ioDispatcher)
    }

    override fun getFeaturedJobs(): Flow<List<Job>> {
        return jobDao.getFeaturedJobs()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun getLatestJobs(limit: Int): Flow<List<Job>> {
        return jobDao.getLatestJobs(limit)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override fun getJobById(id: String): Flow<Job?> {
        return jobDao.getJobById(id)
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)
    }

    override fun getCategories(): Flow<List<JobCategory>> {
        return flow {
            emit(SeedData.categories)
        }.flowOn(ioDispatcher)
    }

    override fun getJobsByCategory(categoryId: String): Flow<List<Job>> {
        return jobDao.getJobsByCategory(categoryId)
            .map { list -> list.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun refreshJobs(): Result<Unit> = withContext(ioDispatcher) {
        try {
            val entities = SeedData.sampleJobs.map { it.toEntity() }
            jobDao.insertJobs(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableLocations(): List<String> = withContext(ioDispatcher) {
        SeedData.locations
    }

    override suspend fun addJob(job: Job): Result<String> = withContext(ioDispatcher) {
        try {
            val finalId = if (job.id.isBlank()) "job_adm_${UUID.randomUUID().toString().take(8)}" else job.id
            val finalJob = job.copy(id = finalId)
            jobDao.insertJob(finalJob.toEntity())
            Result.success(finalId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteJob(jobId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            jobDao.deleteJob(jobId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFeatured(jobId: String, isFeatured: Boolean): Result<Unit> = withContext(ioDispatcher) {
        try {
            jobDao.updateJobFeatured(jobId, isFeatured)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
