package com.gobidev.tmdbv1.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.presentation.castcrew.FullCastCrewScreen
import com.gobidev.tmdbv1.presentation.details.MovieDetailsScreen
import com.gobidev.tmdbv1.presentation.home.HomeScreen
import com.gobidev.tmdbv1.presentation.movielisting.MovieListingScreen
import com.gobidev.tmdbv1.presentation.profile.ProfileScreen
import com.gobidev.tmdbv1.presentation.reviews.MovieReviewsScreen
import com.gobidev.tmdbv1.presentation.search.SearchScreen
import com.gobidev.tmdbv1.presentation.login.LoginScreen
import com.gobidev.tmdbv1.presentation.tvdetails.TvDetailsScreen
import com.gobidev.tmdbv1.presentation.tvdetails.TvFullCastCrewScreen
import com.gobidev.tmdbv1.presentation.persondetails.PersonDetailsScreen
import com.gobidev.tmdbv1.presentation.tvlisting.TvListingScreen

/**
 * Sealed class defining navigation routes in the app.
 *
 * Using sealed class provides type-safety and prevents typos
 * when navigating between screens.
 */
sealed class Screen(val route: String) {
    /**
     * Home screen — entry point with movie carousels.
     */
    data object HomeNav : Screen("home")

    /**
     * Search screen — placeholder.
     */
    data object SearchNav : Screen("search")

    /**
     * Profile screen — placeholder.
     */
    data object ProfileNav : Screen("profile")

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

    /**
     * TV Listing screen — reusable for popular, top_rated, on_the_air, airing_today.
     */
    data object TvListingNav : Screen("tv?listType={listType}") {
        fun createRoute(listType: TvListType) = "tv?listType=${listType.routeKey}"
    }

    /**
     * TV Details screen.
     */
    data object TvDetailsNav : Screen("tv/{tvId}") {
        fun createRoute(tvId: Int) = "tv/$tvId"
    }

    /**
     * TV Cast & Crew screen.
     */
    data object TvCastNav : Screen("tv/{tvId}/cast?tvName={tvName}") {
        fun createRoute(tvId: Int, tvName: String) = "tv/$tvId/cast?tvName=$tvName"
    }

    /**
     * Person Details screen.
     */
    data object PersonDetailsNav : Screen("person/{personId}") {
        fun createRoute(personId: Int) = "person/$personId"
    }

    /**
     * Login screen.
     */
    data object LoginNav : Screen("login")
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
        startDestination = Screen.HomeNav.route
    ) {
        // Home Screen — entry point with movie + TV carousels
        composable(route = Screen.HomeNav.route) {
            HomeScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetailsNav.createRoute(movieId))
                },
                onViewAllClick = { listType ->
                    navController.navigate(Screen.MovieListingNav.createRoute(listType))
                },
                onTvClick = { tvId ->
                    navController.navigate(Screen.TvDetailsNav.createRoute(tvId))
                },
                onViewAllTvClick = { listType ->
                    navController.navigate(Screen.TvListingNav.createRoute(listType))
                }
            )
        }

        // Search Screen
        composable(route = Screen.SearchNav.route) {
            SearchScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetailsNav.createRoute(movieId))
                },
                onTvClick = { tvId ->
                    navController.navigate(Screen.TvDetailsNav.createRoute(tvId))
                },
                onPersonClick = { personId ->
                    navController.navigate(Screen.PersonDetailsNav.createRoute(personId))
                }
            )
        }

        // Profile Screen
        composable(route = Screen.ProfileNav.route) {
            ProfileScreen(
                onLoginClick = {
                    navController.navigate(Screen.LoginNav.route)
                },
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetailsNav.createRoute(movieId))
                }
            )
        }

        // Login Screen
        composable(route = Screen.LoginNav.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginSuccess = { navController.popBackStack() }
            )
        }

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

        // TV Listing Screen
        composable(
            route = Screen.TvListingNav.route,
            arguments = listOf(
                navArgument("listType") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = TvListType.POPULAR.routeKey
                }
            )
        ) {
            TvListingScreen(
                onBackClick = { navController.popBackStack() },
                onTvClick = { tvId ->
                    navController.navigate(Screen.TvDetailsNav.createRoute(tvId))
                }
            )
        }

        // TV Details Screen
        composable(
            route = Screen.TvDetailsNav.route,
            arguments = listOf(
                navArgument("tvId") { type = NavType.IntType }
            )
        ) {
            TvDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onViewFullCastClick = { tvId, tvName ->
                    navController.navigate(Screen.TvCastNav.createRoute(tvId, tvName))
                }
            )
        }

        // Person Details Screen
        composable(
            route = Screen.PersonDetailsNav.route,
            arguments = listOf(
                navArgument("personId") { type = NavType.IntType }
            )
        ) {
            PersonDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetailsNav.createRoute(movieId))
                },
                onTvClick = { tvId ->
                    navController.navigate(Screen.TvDetailsNav.createRoute(tvId))
                }
            )
        }

        // TV Cast & Crew Screen
        composable(
            route = Screen.TvCastNav.route,
            arguments = listOf(
                navArgument("tvId") { type = NavType.IntType },
                navArgument("tvName") {
                    type = NavType.StringType
                    nullable = false
                    defaultValue = "TV Show"
                }
            )
        ) {
            TvFullCastCrewScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
