package com.example.timeinventory.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timeinventory.feature.report.ReportScreen
import com.example.timeinventory.feature.timeline.TimelineScreen
import com.example.timeinventory.navigation.Screen
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import timeinventory.composeapp.generated.resources.Res
import timeinventory.composeapp.generated.resources.bottom_nav_report
import timeinventory.composeapp.generated.resources.bottom_nav_timeline

/**
 * メイン画面
 *
 * NavHostとBottomNavigationを統合した画面
 */
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Timeline,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Screen.Timeline> {
                TimelineScreen()
            }

            composable<Screen.Report> {
                ReportScreen()
            }
        }
    }
}

/**
 * BottomNavigationBar
 */
@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            val label = stringResource(item.labelRes)
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentDestination?.hasRoute(item.route::class) == true,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * BottomNavigationアイテム定義
 */
private data class BottomNavItem(
    val route: Screen,
    val icon: ImageVector,
    val labelRes: StringResource,
)

private val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Timeline,
        icon = Icons.Default.ViewTimeline,
        labelRes = Res.string.bottom_nav_timeline
    ),
    BottomNavItem(
        route = Screen.Report,
        icon = Icons.Default.Analytics,
        labelRes = Res.string.bottom_nav_report
    ),
)
