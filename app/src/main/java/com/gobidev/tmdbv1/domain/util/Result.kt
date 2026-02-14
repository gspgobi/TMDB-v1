package com.gobidev.tmdbv1.domain.util

/**
 * A sealed class that represents the result of an operation.
 *
 * This provides a type-safe way to handle success and error states
 * throughout the application, particularly for use cases and ViewModels.
 */
sealed class Result<out T> {

    /**
     * Represents a successful result containing data.
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Represents an error result containing an error message.
     */
    data class Error(val message: String) : Result<Nothing>()
}

/**
 * Extension function to execute a block and wrap the result in a Result type.
 * Catches exceptions and converts them to Error results.
 */
inline fun <T> safeCall(block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e.message ?: "An unknown error occurred")
    }
}
