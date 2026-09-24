package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.ThemeMode
import com.example.presentation.navigation.LankaJobsAppNavigation
import com.example.ui.theme.LankaJobsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as LankaJobsApp
        val container = app.container

        setContent {
            val themeMode by container.appPreferencesManager.themeMode.collectAsStateWithLifecycle()
            val isDark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            LankaJobsAppTheme(darkTheme = isDark) {
                LankaJobsAppNavigation(container = container)
            }
        }
    }
}
