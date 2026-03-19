package com.gobidev.tmdbv1

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gobidev.tmdbv1.presentation.navigation.Screen
import com.gobidev.tmdbv1.presentation.navigation.TMDBNavGraph
import com.gobidev.tmdbv1.presentation.navigation.parseTmdbUrl
import com.gobidev.tmdbv1.ui.theme.TMDBTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity for the TMDB app.
 *
 * Handles App Links from https://www.themoviedb.org/ and routes them to the
 * correct in-app screen via [parseTmdbUrl].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Pending deep-link URI to navigate to once the NavController is ready.
     * Declared as Compose state so that [LaunchedEffect] inside setContent reacts
     * to changes triggered by [onNewIntent].
     *
     * Only populated on a true cold start (savedInstanceState == null) to avoid
     * re-navigating on configuration changes such as rotation.
     */
    private var pendingDeepLink by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            pendingDeepLink = intent?.data
        }

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

                // Build a synthetic back stack for the deep-linked screen.
                LaunchedEffect(pendingDeepLink) {
                    pendingDeepLink?.let { uri ->
                        parseTmdbUrl(uri)?.let { routes ->
                            // Pop everything back to Home (the start destination),
                            // then push each route in order to build the correct stack.
                            // e.g. /movie/155/cast  →  Home → MovieDetails → Cast
                            navController.popBackStack(
                                route = Screen.HomeNav.route,
                                inclusive = false
                            )
                            routes.forEach { route ->
                                navController.navigate(route)
                            }
                        }
                        pendingDeepLink = null
                    }
                }

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

    /**
     * Called when the activity is already running and a new App Link intent arrives.
     * Works because android:launchMode="singleTop" is set in AndroidManifest.xml.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingDeepLink = intent.data
    }
}
