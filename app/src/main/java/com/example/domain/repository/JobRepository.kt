package com.example.domain.repository

import com.example.domain.model.Job
import com.example.domain.model.JobCategory
import com.example.domain.model.JobFilter
import kotlinx.coroutines.flow.Flow

interface JobRepository {
    fun getJobs(filter: JobFilter = JobFilter()): Flow<List<Job>>
    fun getSuggestedJobs(
        preferredCategories: List<String>,
        preferredLocation: String
    ): Flow<List<Job>>
    fun getFeaturedJobs(): Flow<List<Job>>
    fun getLatestJobs(limit: Int = 20): Flow<List<Job>>
    fun getJobById(id: String): Flow<Job?>
    fun getCategories(): Flow<List<JobCategory>>
    fun getJobsByCategory(categoryId: String): Flow<List<Job>>
    suspend fun refreshJobs(): Result<Unit>
    suspend fun getAvailableLocations(): List<String>
    suspend fun addJob(job: Job): Result<String>
    suspend fun deleteJob(jobId: String): Result<Unit>
    suspend fun toggleFeatured(jobId: String, isFeatured: Boolean): Result<Unit>
}
