package com.gobidev.tmdbv1.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gobidev.tmdbv1.presentation.details.MovieDetailsScreen
import com.gobidev.tmdbv1.presentation.movies.PopularMoviesScreen

/**
 * Sealed class defining navigation routes in the app.
 *
 * Using sealed class provides type-safety and prevents typos
 * when navigating between screens.
 */
sealed class Screen(val route: String) {
    data object PopularMovies : Screen("popular_movies")
    data object MovieDetails : Screen("movie_details/{movieId}") {
        fun createRoute(movieId: Int) = "movie_details/$movieId"
    }
}

/**
 * Main navigation graph for the app.
 *
 * Defines all navigation routes and their corresponding screens.
 * Uses Navigation Compose for declarative navigation.
 *
 * @param navController The navigation controller
 */
@Composable
fun TMDBNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.PopularMovies.route
    ) {
        // Popular Movies Screen
        composable(route = Screen.PopularMovies.route) {
            PopularMoviesScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetails.createRoute(movieId))
                }
            )
        }

        // Movie Details Screen
        composable(
            route = Screen.MovieDetails.route,
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.IntType
                }
            )
        ) {
            MovieDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

