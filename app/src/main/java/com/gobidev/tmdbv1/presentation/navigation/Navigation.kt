package com.gobidev.tmdbv1.presentation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.presentation.collectiondetails.CollectionDetailsEvent
import com.gobidev.tmdbv1.presentation.collectiondetails.CollectionDetailsScreen
import com.gobidev.tmdbv1.presentation.moviedetails.FullCastCrewEvent
import com.gobidev.tmdbv1.presentation.moviedetails.FullCastCrewScreen
import com.gobidev.tmdbv1.presentation.moviedetails.MovieDetailsEvent
import com.gobidev.tmdbv1.presentation.moviedetails.MovieDetailsScreen
import com.gobidev.tmdbv1.presentation.home.HomeEvent
import com.gobidev.tmdbv1.presentation.home.HomeScreen
import com.gobidev.tmdbv1.presentation.login.LoginEvent
import com.gobidev.tmdbv1.presentation.login.LoginScreen
import com.gobidev.tmdbv1.presentation.movielisting.MovieListingEvent
import com.gobidev.tmdbv1.presentation.movielisting.MovieListingScreen
import com.gobidev.tmdbv1.presentation.persondetails.PersonDetailsEvent
import com.gobidev.tmdbv1.presentation.persondetails.PersonDetailsScreen
import com.gobidev.tmdbv1.presentation.profile.ProfileEvent
import com.gobidev.tmdbv1.presentation.profile.ProfileScreen
import com.gobidev.tmdbv1.presentation.reviews.MovieReviewsScreen
import com.gobidev.tmdbv1.presentation.search.SearchEvent
import com.gobidev.tmdbv1.presentation.search.SearchScreen
import com.gobidev.tmdbv1.presentation.tvdetails.TvDetailsEvent
import com.gobidev.tmdbv1.presentation.tvdetails.TvDetailsScreen
import com.gobidev.tmdbv1.presentation.tvdetails.TvFullCastCrewEvent
import com.gobidev.tmdbv1.presentation.tvdetails.TvFullCastCrewScreen
import com.gobidev.tmdbv1.presentation.tvlisting.TvListingEvent
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
     * Collection Details screen.
     */
    data object CollectionDetailsNav : Screen("collection/{collectionId}?collectionName={collectionName}") {
        fun createRoute(collectionId: Int, collectionName: String) =
            "collection/$collectionId?collectionName=${Uri.encode(collectionName)}"
    }

    /**
     * Keyword Movies screen — movies filtered by a single keyword.
     */
    data object KeywordMoviesNav : Screen("movies/keyword/{keywordId}?keywordName={keywordName}") {
        fun createRoute(keywordId: Int, keywordName: String) =
            "movies/keyword/$keywordId?keywordName=${Uri.encode(keywordName)}"
    }

    /**
     * Keyword TV Shows screen — TV shows filtered by a single keyword.
     */
    data object KeywordTvShowsNav : Screen("tv/keyword/{keywordId}?keywordName={keywordName}") {
        fun createRoute(keywordId: Int, keywordName: String) =
            "tv/keyword/$keywordId?keywordName=${Uri.encode(keywordName)}"
    }

    /**
     * Genre Movies screen — movies filtered by a single genre.
     */
    data object GenreMoviesNav : Screen("movies/genre/{genreId}?genreName={genreName}") {
        fun createRoute(genreId: Int, genreName: String) =
            "movies/genre/$genreId?genreName=${Uri.encode(genreName)}"
    }

    /**
     * Genre TV Shows screen — TV shows filtered by a single genre.
     */
    data object GenreTvShowsNav : Screen("tv/genre/{genreId}?genreName={genreName}") {
        fun createRoute(genreId: Int, genreName: String) =
            "tv/genre/$genreId?genreName=${Uri.encode(genreName)}"
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
                onEvent = { event ->
                    when (event) {
                        is HomeEvent.MovieClick -> navController.navigate(Screen.MovieDetailsNav.createRoute(event.movieId))
                        is HomeEvent.ViewAllMoviesClick -> navController.navigate(Screen.MovieListingNav.createRoute(event.listType))
                        is HomeEvent.TvClick -> navController.navigate(Screen.TvDetailsNav.createRoute(event.tvId))
                        is HomeEvent.ViewAllTvClick -> navController.navigate(Screen.TvListingNav.createRoute(event.listType))
                        is HomeEvent.PersonClick -> navController.navigate(Screen.PersonDetailsNav.createRoute(event.personId))
                    }
                }
            )
        }

        // Search Screen
        composable(route = Screen.SearchNav.route) {
            SearchScreen(
                onEvent = { event ->
                    when (event) {
                        is SearchEvent.MovieClick -> navController.navigate(Screen.MovieDetailsNav.createRoute(event.movieId))
                        is SearchEvent.TvClick -> navController.navigate(Screen.TvDetailsNav.createRoute(event.tvId))
                        is SearchEvent.PersonClick -> navController.navigate(Screen.PersonDetailsNav.createRoute(event.personId))
                    }
                }
            )
        }

        // Profile Screen
        composable(route = Screen.ProfileNav.route) {
            ProfileScreen(
                onEvent = { event ->
                    when (event) {
                        is ProfileEvent.LoginClick -> navController.navigate(Screen.LoginNav.route)
                        is ProfileEvent.MovieClick -> navController.navigate(Screen.MovieDetailsNav.createRoute(event.movieId))
                    }
                }
            )
        }

        // Login Screen
        composable(route = Screen.LoginNav.route) {
            LoginScreen(
                onEvent = { event ->
                    when (event) {
                        is LoginEvent.BackClick -> navController.popBackStack()
                        is LoginEvent.LoginSuccess -> navController.popBackStack()
                    }
                }
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
                onEvent = { event ->
                    when (event) {
                        is MovieListingEvent.BackClick -> navController.popBackStack()
                        is MovieListingEvent.MovieClick -> navController.navigate(Screen.MovieDetailsNav.createRoute(event.movieId))
                    }
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
                onEvent = { event ->
                    when (event) {
                        is MovieDetailsEvent.BackClick -> navController.popBackStack()
                        is MovieDetailsEvent.ViewFullCastClick -> navController.navigate(Screen.MovieCastNav.createRoute(event.movieId, event.movieTitle))
                        is MovieDetailsEvent.ViewAllReviewsClick -> navController.navigate(Screen.MovieReviewsNav.createRoute(event.movieId, event.movieTitle))
                        is MovieDetailsEvent.CastMemberClick -> navController.navigate(Screen.PersonDetailsNav.createRoute(event.personId))
                        is MovieDetailsEvent.CollectionClick -> navController.navigate(Screen.CollectionDetailsNav.createRoute(event.collectionId, event.collectionName))
                        is MovieDetailsEvent.RecommendationClick -> navController.navigate(Screen.MovieDetailsNav.createRoute(event.movieId))
                        is MovieDetailsEvent.KeywordClick -> navController.navigate(Screen.KeywordMoviesNav.createRoute(event.keywordId, event.keywordName))
                        is MovieDetailsEvent.GenreClick -> navController.navigate(Screen.GenreMoviesNav.createRoute(event.genreId, event.genreName))
                    }
                }
            )
        }

        // Keyword Movies Screen — reuses MovieListingScreen with keyword pre-filter
        composable(
            route = Screen.KeywordMoviesNav.route,
            arguments = listOf(
                navArgument("keywordId") { type = NavType.IntType },
                navArgument("keywordName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) {
            MovieListingScreen(
                onEvent = { event ->
                    when (event) {
                        is MovieListingEvent.BackClick -> navController.popBackStack()
                        is MovieListingEvent.MovieClick -> navController.navigate(Screen.MovieDetailsNav.createRoute(event.movieId))
                    }
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
                onEvent = { event ->
                    when (event) {
                        is FullCastCrewEvent.BackClick -> navController.popBackStack()
                        is FullCastCrewEvent.PersonClick -> navController.navigate(Screen.PersonDetailsNav.createRoute(event.personId))
                    }
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
                onEvent = { event ->
                    when (event) {
                        is TvListingEvent.BackClick -> navController.popBackStack()
                        is TvListingEvent.TvClick -> navController.navigate(Screen.TvDetailsNav.createRoute(event.tvId))
                    }
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
                onEvent = { event ->
                    when (event) {
                        is TvDetailsEvent.BackClick -> navController.popBackStack()
                        is TvDetailsEvent.ViewFullCastClick -> navController.navigate(Screen.TvCastNav.createRoute(event.tvId, event.tvName))
                        is TvDetailsEvent.CastMemberClick -> navController.navigate(Screen.PersonDetailsNav.createRoute(event.personId))
                        is TvDetailsEvent.RecommendationClick -> navController.navigate(Screen.TvDetailsNav.createRoute(event.tvId))
                        is TvDetailsEvent.KeywordClick -> navController.navigate(Screen.KeywordTvShowsNav.createRoute(event.keywordId, event.keywordName))
                        is TvDetailsEvent.GenreClick -> navController.navigate(Screen.GenreTvShowsNav.createRoute(event.genreId, event.genreName))
                    }
                }
            )
        }

        // Keyword TV Shows Screen — reuses TvListingScreen with keyword pre-filter
        composable(
            route = Screen.KeywordTvShowsNav.route,
            arguments = listOf(
                navArgument("keywordId") { type = NavType.IntType },
                navArgument("keywordName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) {
            TvListingScreen(
                onEvent = { event ->
                    when (event) {
                        is TvListingEvent.BackClick -> navController.popBackStack()
                        is TvListingEvent.TvClick -> navController.navigate(Screen.TvDetailsNav.createRoute(event.tvId))
                    }
                }
            )
        }

        // Genre Movies Screen — reuses MovieListingScreen with genre pre-filter
        composable(
            route = Screen.GenreMoviesNav.route,
            arguments = listOf(
                navArgument("genreId") { type = NavType.IntType },
                navArgument("genreName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) {
            MovieListingScreen(
                onEvent = { event ->
                    when (event) {
                        is MovieListingEvent.BackClick -> navController.popBackStack()
                        is MovieListingEvent.MovieClick -> navController.navigate(Screen.MovieDetailsNav.createRoute(event.movieId))
                    }
                }
            )
        }

        // Genre TV Shows Screen — reuses TvListingScreen with genre pre-filter
        composable(
            route = Screen.GenreTvShowsNav.route,
            arguments = listOf(
                navArgument("genreId") { type = NavType.IntType },
                navArgument("genreName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) {
            TvListingScreen(
                onEvent = { event ->
                    when (event) {
                        is TvListingEvent.BackClick -> navController.popBackStack()
                        is TvListingEvent.TvClick -> navController.navigate(Screen.TvDetailsNav.createRoute(event.tvId))
                    }
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
                onEvent = { event ->
                    when (event) {
                        is PersonDetailsEvent.BackClick -> navController.popBackStack()
                        is PersonDetailsEvent.MovieClick -> navController.navigate(Screen.MovieDetailsNav.createRoute(event.movieId))
                        is PersonDetailsEvent.TvClick -> navController.navigate(Screen.TvDetailsNav.createRoute(event.tvId))
                    }
                }
            )
        }

        // Collection Details Screen
        composable(
            route = Screen.CollectionDetailsNav.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.IntType },
                navArgument("collectionName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) {
            CollectionDetailsScreen(
                onEvent = { event ->
                    when (event) {
                        is CollectionDetailsEvent.BackClick -> navController.popBackStack()
                        is CollectionDetailsEvent.MovieClick -> navController.navigate(Screen.MovieDetailsNav.createRoute(event.movieId))
                    }
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
            TvFullCastCrewScreen(
                onEvent = { event ->
                    when (event) {
                        is TvFullCastCrewEvent.BackClick -> navController.popBackStack()
                        is TvFullCastCrewEvent.PersonClick -> navController.navigate(Screen.PersonDetailsNav.createRoute(event.personId))
                    }
                }
            )
        }
    }
}
