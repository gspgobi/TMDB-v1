package com.gobidev.tmdbv1.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.presentation.castcrew.FullCastCrewScreen
import com.gobidev.tmdbv1.presentation.details.MovieDetailsScreen
import com.gobidev.tmdbv1.presentation.movielisting.MovieListingScreen
import com.gobidev.tmdbv1.presentation.reviews.MovieReviewsScreen

/**
 * Sealed class defining navigation routes in the app.
 *
 * Using sealed class provides type-safety and prevents typos
 * when navigating between screens.
 */
sealed class Screen(val route: String) {
    /**
     * Movie Listing screen — reusable for popular, now_playing, top_rated, upcoming.
     */
    data object MovieListingNav : Screen("movies?listType={listType}") {
        fun createRoute(listType: MovieListType) = "movies?listType=${listType.routeKey}"
    }

    /**
     * Movie Details screen.
     */
    data object MovieDetailsNav : Screen("movie/{movieId}") {
        fun createRoute(movieId: Int) = "movie/$movieId"
    }

    /**
     * Movie Cast & Crew screen.
     * Uses query parameter for movie title to avoid URL encoding issues.
     */
    data object MovieCastNav : Screen("movie/{movieId}/cast?movieTitle={movieTitle}") {
        fun createRoute(movieId: Int, movieTitle: String): String {
            // No URL encoding needed - query parameters handle special characters automatically
            return "movie/$movieId/cast?movieTitle=$movieTitle"
        }
    }

    /**
     * Movie Reviews screen - shows all reviews for a movie with pagination.
     * Uses query parameter for movie title.
     */
    data object MovieReviewsNav : Screen("movie/{movieId}/reviews?movieTitle={movieTitle}") {
        fun createRoute(movieId: Int, movieTitle: String): String {
            return "movie/$movieId/reviews?movieTitle=$movieTitle"
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
        startDestination = Screen.MovieListingNav.createRoute(MovieListType.POPULAR)
    ) {
        // Movie Listing Screen — handles popular, now_playing, top_rated, upcoming
        composable(
            route = Screen.MovieListingNav.route,
            arguments = listOf(
                navArgument("listType") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = MovieListType.POPULAR.routeKey
                }
            )
        ) {
            MovieListingScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetailsNav.createRoute(movieId))
                }
            )
        }

        // Movie Details Screen
        composable(
            route = Screen.MovieDetailsNav.route,
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
                    navController.navigate(Screen.MovieCastNav.createRoute(movieId, movieTitle))
                },
                onViewAllReviewsClick = { movieId, movieTitle ->
                    navController.navigate(
                        Screen.MovieReviewsNav.createRoute(
                            movieId,
                            movieTitle
                        )
                    )
                }
            )
        }

        // Movie Cast & Crew Screen
        composable(
            route = Screen.MovieCastNav.route,
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

        // Movie Reviews Screen
        composable(
            route = Screen.MovieReviewsNav.route,
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
            MovieReviewsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
