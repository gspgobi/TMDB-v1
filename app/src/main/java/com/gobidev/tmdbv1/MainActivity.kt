package com.gobidev.tmdbv1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
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
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    TMDBNavGraph(navController = navController)
                }
            }
        }
    }
}
