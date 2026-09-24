package com.example.data.repository

import com.example.data.local.LankaJobsDatabase
import com.example.data.local.entity.SavedJobEntity
import com.example.data.mapper.toDomain
import com.example.domain.model.Job
import com.example.domain.repository.JobRepository
import com.example.domain.repository.SavedJobRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SavedJobRepositoryImpl(
    private val database: LankaJobsDatabase,
    private val jobRepository: JobRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SavedJobRepository {

    private val savedJobDao = database.savedJobDao()

    override fun getSavedJobIds(): Flow<Set<String>> {
        return savedJobDao.getSavedJobIds()
            .map { it.toSet() }
            .flowOn(ioDispatcher)
    }

    override fun getSavedJobs(): Flow<List<Job>> {
        return savedJobDao.getSavedJobs()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)
    }

    override suspend fun toggleSaveJob(jobId: String): Boolean = withContext(ioDispatcher) {
        val exists = savedJobDao.isJobSaved(jobId)
        if (exists) {
            savedJobDao.removeSavedJob(jobId)
            false
        } else {
            savedJobDao.saveJob(SavedJobEntity(jobId = jobId))
            true
        }
    }

    override suspend fun isJobSaved(jobId: String): Boolean = withContext(ioDispatcher) {
        savedJobDao.isJobSaved(jobId)
    }

    override suspend fun removeSavedJob(jobId: String): Unit = withContext(ioDispatcher) {
        savedJobDao.removeSavedJob(jobId)
    }
}
