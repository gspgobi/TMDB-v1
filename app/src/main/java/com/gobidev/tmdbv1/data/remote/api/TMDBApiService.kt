package com.gobidev.tmdbv1.data.remote.api

import com.gobidev.tmdbv1.data.remote.dto.MovieCreditsResponse
import com.gobidev.tmdbv1.data.remote.dto.MovieDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.MovieReviewsPagedResponse
import com.gobidev.tmdbv1.data.remote.dto.MovieListPagedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for TMDB API endpoints.
 *
 * Authentication is handled via OkHttp interceptor which adds the
 * Authorization header to all requests automatically.
 */
interface TMDBApiService {

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_genres") withGenres: String? = null,
        @Query("vote_average.gte") voteAverageGte: Double? = null,
        @Query("primary_release_year") primaryReleaseYear: Int? = null
    ): MovieListPagedResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListPagedResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListPagedResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListPagedResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieListPagedResponse

    /**
     * Fetch detailed information for a specific movie.
     *
     * @param movieId The ID of the movie to fetch
     * @param language Language code (default: en-US)
     * @return Detailed movie information
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): MovieDetailsResponse

    /**
     * Fetch cast and crew credits for a specific movie.
     *
     * @param movieId The ID of the movie to fetch credits for
     * @param language Language code (default: en-US)
     * @return Movie credits containing cast and crew information
     */
    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): MovieCreditsResponse

    /**
     * Fetch user reviews for a specific movie with pagination.
     *
     * @param movieId The ID of the movie to fetch reviews for
     * @param language Language code (default: en-US)
     * @param page Page number for pagination (starts at 1)
     * @return Response containing list of reviews
     */
    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieReviewsPagedResponse
}
