package com.example.domain.repository

import com.example.domain.model.Job
import kotlinx.coroutines.flow.Flow

interface SavedJobRepository {
    fun getSavedJobIds(): Flow<Set<String>>
    fun getSavedJobs(): Flow<List<Job>>
    suspend fun toggleSaveJob(jobId: String): Boolean
    suspend fun isJobSaved(jobId: String): Boolean
    suspend fun removeSavedJob(jobId: String)
}
