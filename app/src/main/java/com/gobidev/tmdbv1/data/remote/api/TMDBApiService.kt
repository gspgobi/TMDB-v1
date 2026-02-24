package com.gobidev.tmdbv1.data.remote.api

import com.gobidev.tmdbv1.data.remote.dto.MovieCreditsDto
import com.gobidev.tmdbv1.data.remote.dto.MovieDetailsDto
import com.gobidev.tmdbv1.data.remote.dto.MovieReviewsResponse
import com.gobidev.tmdbv1.data.remote.dto.PopularMoviesResponse
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

    /**
     * Fetch popular movies with pagination support.
     *
     * @param language Language code (default: en-US)
     * @param page Page number for pagination (starts at 1)
     * @return Response containing list of popular movies
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): PopularMoviesResponse

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
    ): MovieDetailsDto

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
    ): MovieCreditsDto

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
    ): MovieReviewsResponse
}
