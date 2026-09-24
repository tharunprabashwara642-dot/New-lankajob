package com.example

import android.app.Application
import com.example.core.AppContainer
import com.example.core.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LankaJobsApp : Application() {
    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        // Pre-seed offline database in background so UI operations never block or stutter
        applicationScope.launch {
            container.preseedDatabaseIfNeeded()
        }
    }
}
