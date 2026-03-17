package com.gobidev.tmdbv1.data.remote.api

import com.gobidev.tmdbv1.data.remote.dto.PersonCombinedCreditsResponse
import com.gobidev.tmdbv1.data.remote.dto.PersonDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.AccountResponse
import com.gobidev.tmdbv1.data.remote.dto.DeleteSessionBody
import com.gobidev.tmdbv1.data.remote.dto.LoginRequestBody
import com.gobidev.tmdbv1.data.remote.dto.MovieCreditsResponse
import com.gobidev.tmdbv1.data.remote.dto.MovieDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.MovieListPagedResponse
import com.gobidev.tmdbv1.data.remote.dto.MovieReviewsPagedResponse
import com.gobidev.tmdbv1.data.remote.dto.RequestTokenResponse
import com.gobidev.tmdbv1.data.remote.dto.SearchResultPagedResponse
import com.gobidev.tmdbv1.data.remote.dto.SessionRequestBody
import com.gobidev.tmdbv1.data.remote.dto.SessionResponse
import com.gobidev.tmdbv1.data.remote.dto.SeasonDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.TvCreditsResponse
import com.gobidev.tmdbv1.data.remote.dto.TvDetailsResponse
import com.gobidev.tmdbv1.data.remote.dto.TvListPagedResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
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

    // ── TV Series ────────────────────────────────────────────────────────────

    @GET("tv/popular")
    suspend fun getPopularTv(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TvListPagedResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedTv(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TvListPagedResponse

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTv(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TvListPagedResponse

    @GET("tv/airing_today")
    suspend fun getAiringTodayTv(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TvListPagedResponse

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(
        @Path("tv_id") tvId: Int,
        @Query("language") language: String = "en-US"
    ): TvDetailsResponse

    @GET("tv/{tv_id}/credits")
    suspend fun getTvCredits(
        @Path("tv_id") tvId: Int,
        @Query("language") language: String = "en-US"
    ): TvCreditsResponse

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getSeasonDetails(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("language") language: String = "en-US"
    ): SeasonDetailsResponse

    // ── Person ───────────────────────────────────────────────────────────────

    @GET("person/{person_id}")
    suspend fun getPersonDetails(
        @Path("person_id") personId: Int,
        @Query("language") language: String = "en-US"
    ): PersonDetailsResponse

    @GET("person/{person_id}/combined_credits")
    suspend fun getPersonCombinedCredits(
        @Path("person_id") personId: Int,
        @Query("language") language: String = "en-US"
    ): PersonCombinedCreditsResponse

    // ── Search ───────────────────────────────────────────────────────────────

    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): SearchResultPagedResponse

    // ── Auth ─────────────────────────────────────────────────────────────────

    @GET("authentication/token/new")
    suspend fun createRequestToken(): RequestTokenResponse

    @POST("authentication/token/validate_with_login")
    suspend fun validateWithLogin(@Body body: LoginRequestBody): RequestTokenResponse

    @POST("authentication/session/new")
    suspend fun createSession(@Body body: SessionRequestBody): SessionResponse

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(@Body body: DeleteSessionBody)

    // ── Account ───────────────────────────────────────────────────────────────

    @GET("account")
    suspend fun getAccount(
        @Query("session_id") sessionId: String
    ): AccountResponse

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1
    ): MovieListPagedResponse

    @GET("account/{account_id}/watchlist/movies")
    suspend fun getWatchlistMovies(
        @Path("account_id") accountId: Int,
        @Query("session_id") sessionId: String,
        @Query("page") page: Int = 1
    ): MovieListPagedResponse
}
