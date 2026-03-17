package com.gobidev.tmdbv1.di

import com.gobidev.tmdbv1.BuildConfig
import com.gobidev.tmdbv1.data.remote.api.AuthInterceptor
import com.gobidev.tmdbv1.data.remote.api.TMDBApiService
import com.gobidev.tmdbv1.data.repository.PersonRepositoryImpl
import com.gobidev.tmdbv1.data.repository.MovieRepositoryImpl
import com.gobidev.tmdbv1.data.repository.AccountRepositoryImpl
import com.gobidev.tmdbv1.data.repository.AuthRepositoryImpl
import com.gobidev.tmdbv1.data.repository.SearchRepositoryImpl
import com.gobidev.tmdbv1.data.repository.TvRepositoryImpl
import com.gobidev.tmdbv1.domain.repository.AccountRepository
import com.gobidev.tmdbv1.domain.repository.PersonRepository
import com.gobidev.tmdbv1.domain.repository.AuthRepository
import com.gobidev.tmdbv1.domain.repository.MovieRepository
import com.gobidev.tmdbv1.domain.repository.SearchRepository
import com.gobidev.tmdbv1.domain.repository.TvRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for providing network and repository dependencies.
 *
 * @InstallIn(SingletonComponent::class) means these dependencies
 * will live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Base URL for TMDB API.
     */
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    /**
     * Provide OkHttpClient with authentication and logging interceptors.
     *
     * - AuthInterceptor: Adds Bearer token to all requests
     * - HttpLoggingInterceptor: Logs network requests/responses (debug only)
     *
     * @return Configured OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(BuildConfig.TMDB_API_TOKEN))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provide Retrofit instance configured for TMDB API.
     *
     * Uses Gson for JSON serialization/deserialization.
     *
     * @param okHttpClient The OkHttp client with interceptors
     * @return Configured Retrofit instance
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provide TMDB API service.
     *
     * Retrofit creates the implementation of the interface at runtime.
     *
     * @param retrofit The Retrofit instance
     * @return TMDB API service implementation
     */
    @Provides
    @Singleton
    fun provideTMDBApiService(retrofit: Retrofit): TMDBApiService {
        return retrofit.create(TMDBApiService::class.java)
    }

    /**
     * Provide MovieRepository implementation.
     *
     * Binds the concrete implementation to the interface.
     * This allows the domain layer to depend on the interface,
     * not the implementation.
     *
     * @param repositoryImpl The repository implementation
     * @return MovieRepository interface
     */
    @Provides
    @Singleton
    fun provideMovieRepository(
        repositoryImpl: MovieRepositoryImpl
    ): MovieRepository {
        return repositoryImpl
    }

    @Provides
    @Singleton
    fun provideTvRepository(
        repositoryImpl: TvRepositoryImpl
    ): TvRepository {
        return repositoryImpl
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        repositoryImpl: SearchRepositoryImpl
    ): SearchRepository {
        return repositoryImpl
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        repositoryImpl: AuthRepositoryImpl
    ): AuthRepository {
        return repositoryImpl
    }

    @Provides
    @Singleton
    fun provideAccountRepository(
        repositoryImpl: AccountRepositoryImpl
    ): AccountRepository {
        return repositoryImpl
    }

    @Provides
    @Singleton
    fun providePersonRepository(
        repositoryImpl: PersonRepositoryImpl
    ): PersonRepository {
        return repositoryImpl
    }
}
