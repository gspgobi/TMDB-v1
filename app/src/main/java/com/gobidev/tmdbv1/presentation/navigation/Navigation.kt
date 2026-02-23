package com.gobidev.tmdbv1.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gobidev.tmdbv1.presentation.castcrew.FullCastCrewScreen
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

    /**
     * Full Cast & Crew screen - nested under movie details.
     * Uses query parameter for movie title to avoid URL encoding issues.
     *
     * Query parameters automatically handle special characters like '/', '&', etc.
     */
    data object FullCastCrew : Screen("movie_details/{movieId}/full_cast_crew?movieTitle={movieTitle}") {
        fun createRoute(movieId: Int, movieTitle: String): String {
            // No URL encoding needed - query parameters handle special characters automatically
            return "movie_details/$movieId/full_cast_crew?movieTitle=$movieTitle"
        }
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
                },
                onViewFullCastClick = { movieId, movieTitle ->
                    navController.navigate(Screen.FullCastCrew.createRoute(movieId, movieTitle))
                }
            )
        }

        // Full Cast & Crew Screen - nested under movie details
        composable(
            route = Screen.FullCastCrew.route,
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.IntType
                },
                navArgument("movieTitle") {
                    type = NavType.StringType
                    nullable = false
                    defaultValue = "Movie"
                }
            )
        ) {
            FullCastCrewScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
