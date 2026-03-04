package com.gobidev.tmdbv1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gobidev.tmdbv1.presentation.navigation.Screen
import com.gobidev.tmdbv1.presentation.navigation.TMDBNavGraph
import com.gobidev.tmdbv1.ui.theme.TMDBTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the TMDB app.
 *
 * @AndroidEntryPoint enables Hilt dependency injection in this Activity.
 * This allows ViewModels and other dependencies to be injected.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TMDBTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute in setOf(
                    Screen.HomeNav.route,
                    Screen.SearchNav.route,
                    Screen.ProfileNav.route
                )

                Scaffold(
                    contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.HomeNav.route,
                                    onClick = {
                                        navController.navigate(Screen.HomeNav.route) {
                                            popUpTo(Screen.HomeNav.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Home") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.SearchNav.route,
                                    onClick = {
                                        navController.navigate(Screen.SearchNav.route) {
                                            popUpTo(Screen.HomeNav.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                    label = { Text("Search") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.ProfileNav.route,
                                    onClick = {
                                        navController.navigate(Screen.ProfileNav.route) {
                                            popUpTo(Screen.HomeNav.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                    label = { Text("Profile") }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        TMDBNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
