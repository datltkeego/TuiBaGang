package com.etu.tuibagang.navagation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.etu.tuibagang.data.model.ApkItem
import com.etu.tuibagang.feature.versions.SortDirection
import com.etu.tuibagang.ui.apkdetail.PlaceholderScreen
import com.etu.tuibagang.ui.versiondetail.VersionDetailScreen
import com.etu.tuibagang.ui.apkdetail.VersionsScreenContent

private enum class AppTab(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
) {
    Versions(
        route = "versions",
        label = "Versions",
        icon = { Icon(Icons.Default.Build, contentDescription = null) }
    ),
    Products(
        route = "products",
        label = "Products",
        icon = { Icon(Icons.Default.Apps, contentDescription = null) }
    ),
    Settings(
        route = "settings",
        label = "Settings",
        icon = { Icon(Icons.Default.Settings, contentDescription = null) }
    )
}

@Composable
internal fun AppRootContent(
    apks: List<ApkItem>,
    isRefreshing: Boolean,
    downloadingToken: String?,
    error: String?,
    searchQuery: String = "",
    sortByDate: SortDirection = SortDirection.NONE,
    sortByName: SortDirection = SortDirection.NONE,
    onSearchQueryChange: (String) -> Unit = {},
    onToggleSortByDate: () -> Unit = {},
    onToggleSortByName: () -> Unit = {},
    onRefresh: () -> Unit,
    onInstallApk: (String, String) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val destination = navBackStackEntry?.destination
            val currentRoute = destination?.route

            if (currentRoute?.startsWith("version_detail") != true) {
                NavigationBar {
                    AppTab.entries.forEach { tab ->
                        val selected =
                            destination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = tab.icon,
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppTab.Versions.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppTab.Versions.route) {
                VersionsScreenContent(
                        apks = apks,
                        isRefreshing = isRefreshing,
                        downloadingToken = downloadingToken,
                        error = error,
                        searchQuery = searchQuery,
                        sortByDate = sortByDate,
                        sortByName = sortByName,
                        onSearchQueryChange = onSearchQueryChange,
                        onToggleSortByDate = onToggleSortByDate,
                        onToggleSortByName = onToggleSortByName,
                        onRefresh = onRefresh,
                        onInstallApk = onInstallApk,
                        onOpenDetail = { apkId ->
                            navController.navigate("version_detail/$apkId")
                        })
            }
            composable(
                route = "version_detail/{apkId}",
                arguments = listOf(navArgument("apkId") { type = NavType.LongType })
            ) { backStackEntry ->
                val apkId = backStackEntry.arguments?.getLong("apkId")
                val item = apks.firstOrNull { it.id == apkId }
                VersionDetailScreen(
                        item = item,
                        downloadingToken = downloadingToken,
                        onBack = { navController.popBackStack() },
                        onInstallApk = onInstallApk
                )
            }
            composable(AppTab.Products.route) {
                PlaceholderScreen(title = "Products")
            }
            composable(AppTab.Settings.route) {
                PlaceholderScreen(title = "Settings")
            }
        }
    }
}
