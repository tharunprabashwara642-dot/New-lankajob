package com.example.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.AppContainer
import com.example.domain.model.UserProfile
import com.example.presentation.ads.AdDetailsScreen
import com.example.presentation.ads.AdsListScreen
import com.example.presentation.ads.AdsViewModel
import com.example.presentation.ads.CreateAdScreen
import com.example.presentation.ads.MyAdsScreen
import com.example.presentation.auth.CompleteProfileScreen
import com.example.presentation.auth.SignInScreen
import com.example.presentation.components.MaintenanceScreen
import com.example.presentation.home.HomeScreen
import com.example.presentation.home.HomeViewModel
import com.example.presentation.jobs.CategoryJobsScreen
import com.example.presentation.jobs.JobDetailsScreen
import com.example.presentation.jobs.JobDetailsViewModel
import com.example.presentation.notifications.NotificationSettingsScreen
import com.example.presentation.profile.EditProfileScreen
import com.example.presentation.profile.MyPaymentsScreen
import com.example.presentation.profile.ProfileScreen
import com.example.presentation.profile.ProfileViewModel
import com.example.presentation.saved.SavedJobsScreen
import com.example.presentation.saved.SavedJobsViewModel
import com.example.presentation.search.SearchScreen
import com.example.presentation.search.SearchViewModel
import com.example.ui.theme.LankaJobsTheme

@Composable
fun LankaJobsAppNavigation(
    container: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    val tokens = LankaJobsTheme.tokens
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val appSettings by container.appPreferencesManager.appSettings.collectAsStateWithLifecycle()
    var currentUser by remember { mutableStateOf<UserProfile?>(null) }
    var isAuthLoaded by remember { mutableStateOf(false) }

    // Observe user profile state
    LaunchedEffect(Unit) {
        container.authRepository.currentUser.collect { user ->
            currentUser = user
            isAuthLoaded = true
        }
    }

    // Maintenance Mode Check (Admin Web controlled)
    if (appSettings.maintenanceMode) {
        MaintenanceScreen(
            onRetry = {
                // Trigger refresh if needed
            }
        )
        return
    }

    if (!isAuthLoaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(tokens.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LankaJobs",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = tokens.primary
                )
                Spacer(modifier = Modifier.height(14.dp))
                CircularProgressIndicator(
                    color = tokens.primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        return
    }

    // Browsing jobs must work without login per spec; Home is initial start destination
    val startDestination = Screen.Home.route

    val isBottomBarVisible = currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Saved.route,
        Screen.Profile.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = tokens.surface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    bottomNavDestinations.forEach { dest ->
                        val isSelected = currentRoute == dest.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != dest.route) {
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                                    contentDescription = dest.label
                                )
                            },
                            label = {
                                Text(
                                    text = dest.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = tokens.primary,
                                selectedTextColor = tokens.primary,
                                unselectedIconColor = tokens.textMuted,
                                unselectedTextColor = tokens.textMuted,
                                indicatorColor = tokens.primaryContainer
                            ),
                            modifier = Modifier.testTag(dest.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Sign In Destination
            composable(Screen.SignIn.route) {
                val profileViewModel = remember {
                    ProfileViewModel(authRepository = container.authRepository)
                }

                SignInScreen(
                    viewModel = profileViewModel,
                    onRequiresProfileCompletion = {
                        navController.navigate(Screen.CompleteProfile.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    },
                    onSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 2. Complete Profile Destination
            composable(Screen.CompleteProfile.route) {
                val profileViewModel = remember {
                    ProfileViewModel(authRepository = container.authRepository)
                }

                CompleteProfileScreen(
                    viewModel = profileViewModel,
                    onProfileCompleted = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 3. Home Destination
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel {
                    HomeViewModel(
                        jobRepository = container.jobRepository,
                        savedJobRepository = container.savedJobRepository,
                        advertisementRepository = container.advertisementRepository,
                        authRepository = container.authRepository
                    )
                }

                HomeScreen(
                    viewModel = homeViewModel,
                    onJobClick = { jobId ->
                        navController.navigate(Screen.JobDetails.createRoute(jobId))
                    },
                    onCategoryClick = { categoryId, categoryName ->
                        navController.navigate(Screen.CategoryJobs.createRoute(categoryId, categoryName))
                    },
                    onSearchClick = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNotificationsClick = {
                        navController.navigate(Screen.NotificationSettings.route)
                    },
                    onAdClick = { adId ->
                        navController.navigate(Screen.AdDetails.createRoute(adId))
                    },
                    onBrowseAdsClick = {
                        navController.navigate(Screen.AdsList.route)
                    }
                )
            }

            // 4. Search Destination
            composable(Screen.Search.route) {
                val searchViewModel: SearchViewModel = viewModel {
                    SearchViewModel(
                        jobRepository = container.jobRepository,
                        savedJobRepository = container.savedJobRepository
                    )
                }

                SearchScreen(
                    viewModel = searchViewModel,
                    onJobClick = { jobId ->
                        navController.navigate(Screen.JobDetails.createRoute(jobId))
                    }
                )
            }

            // 5. Saved Jobs Destination
            composable(Screen.Saved.route) {
                val savedViewModel: SavedJobsViewModel = viewModel {
                    SavedJobsViewModel(savedJobRepository = container.savedJobRepository)
                }

                SavedJobsScreen(
                    viewModel = savedViewModel,
                    onJobClick = { jobId ->
                        navController.navigate(Screen.JobDetails.createRoute(jobId))
                    },
                    onExploreJobsClick = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }

            // 6. Profile Destination
            composable(Screen.Profile.route) {
                val profileViewModel = remember {
                    ProfileViewModel(
                        authRepository = container.authRepository,
                        appPreferencesManager = container.appPreferencesManager
                    )
                }

                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToSignIn = {
                        navController.navigate(Screen.SignIn.route)
                    },
                    onNavigateToEditProfile = {
                        navController.navigate(Screen.EditProfile.route)
                    },
                    onNavigateToMyAds = {
                        navController.navigate(Screen.MyAds.route)
                    },
                    onNavigateToMyPayments = {
                        navController.navigate(Screen.MyPayments.route)
                    },
                    onNavigateToSavedJobs = {
                        navController.navigate(Screen.Saved.route)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.NotificationSettings.route)
                    },
                    onSignedOut = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 7. Job Details Destination
            composable(
                route = Screen.JobDetails.route,
                arguments = listOf(navArgument("jobId") { type = NavType.StringType })
            ) { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                val detailsViewModel = remember(jobId) {
                    JobDetailsViewModel(
                        jobId = jobId,
                        jobRepository = container.jobRepository,
                        savedJobRepository = container.savedJobRepository
                    )
                }

                JobDetailsScreen(
                    viewModel = detailsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 8. Category Jobs Destination
            composable(
                route = Screen.CategoryJobs.route,
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.StringType },
                    navArgument("categoryName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""

                CategoryJobsScreen(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    jobRepository = container.jobRepository,
                    savedJobRepository = container.savedJobRepository,
                    onJobClick = { jobId ->
                        navController.navigate(Screen.JobDetails.createRoute(jobId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 9. Edit Profile Destination
            composable(Screen.EditProfile.route) {
                val profileViewModel = remember {
                    ProfileViewModel(authRepository = container.authRepository)
                }

                EditProfileScreen(
                    viewModel = profileViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 10. Ads List Destination
            composable(Screen.AdsList.route) {
                val adsViewModel = remember {
                    AdsViewModel(
                        advertisementRepository = container.advertisementRepository,
                        paymentRepository = container.paymentRepository,
                        authRepository = container.authRepository
                    )
                }

                AdsListScreen(
                    viewModel = adsViewModel,
                    onAdClick = { adId ->
                        navController.navigate(Screen.AdDetails.createRoute(adId))
                    },
                    onCreateAdClick = {
                        if (currentUser == null) {
                            navController.navigate(Screen.SignIn.route)
                        } else {
                            navController.navigate(Screen.CreateAd.route)
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 11. Ad Details Destination
            composable(
                route = Screen.AdDetails.route,
                arguments = listOf(navArgument("adId") { type = NavType.StringType })
            ) { backStackEntry ->
                val adId = backStackEntry.arguments?.getString("adId") ?: ""

                AdDetailsScreen(
                    adId = adId,
                    advertisementRepository = container.advertisementRepository,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 12. Create Advertisement Destination
            composable(Screen.CreateAd.route) {
                val adsViewModel = remember {
                    AdsViewModel(
                        advertisementRepository = container.advertisementRepository,
                        paymentRepository = container.paymentRepository,
                        authRepository = container.authRepository
                    )
                }

                CreateAdScreen(
                    viewModel = adsViewModel,
                    onSuccess = {
                        navController.navigate(Screen.MyAds.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 13. My Advertisements Destination
            composable(Screen.MyAds.route) {
                MyAdsScreen(
                    userId = currentUser?.id ?: "usr_current",
                    advertisementRepository = container.advertisementRepository,
                    onAdClick = { adId ->
                        navController.navigate(Screen.AdDetails.createRoute(adId))
                    },
                    onCreateAdClick = {
                        navController.navigate(Screen.CreateAd.route)
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 14. My Payments Destination
            composable(Screen.MyPayments.route) {
                MyPaymentsScreen(
                    userId = currentUser?.id ?: "usr_current",
                    paymentRepository = container.paymentRepository,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 15. Notification Settings Destination
            composable(Screen.NotificationSettings.route) {
                NotificationSettingsScreen(
                    notificationRepository = container.notificationRepository,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
