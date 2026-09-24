package com.example.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Saved : Screen("saved")
    data object Profile : Screen("profile")

    data object JobDetails : Screen("job/{jobId}") {
        fun createRoute(jobId: String) = "job/$jobId"
    }

    data object CategoryJobs : Screen("category/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: String, categoryName: String) = "category/$categoryId/$categoryName"
    }

    data object SignIn : Screen("auth/signin")
    data object CompleteProfile : Screen("auth/complete_profile")
    data object EditProfile : Screen("profile/edit")

    data object AdsList : Screen("ads")
    data object AdDetails : Screen("ads/{adId}") {
        fun createRoute(adId: String) = "ads/$adId"
    }
    data object CreateAd : Screen("ads/create")
    data object MyAds : Screen("ads/my")
    data object MyPayments : Screen("payments/my")

    data object NotificationSettings : Screen("notifications/settings")
}

data class BottomNavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val bottomNavDestinations = listOf(
    BottomNavDestination(
        route = Screen.Home.route,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_tab_home"
    ),
    BottomNavDestination(
        route = Screen.Search.route,
        label = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
        testTag = "nav_tab_search"
    ),
    BottomNavDestination(
        route = Screen.Saved.route,
        label = "Saved",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Filled.BookmarkBorder,
        testTag = "nav_tab_saved"
    ),
    BottomNavDestination(
        route = Screen.Profile.route,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_tab_profile"
    )
)
