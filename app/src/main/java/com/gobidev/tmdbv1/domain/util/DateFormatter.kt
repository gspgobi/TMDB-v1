package com.gobidev.tmdbv1.domain.util

import java.text.SimpleDateFormat
import java.util.Locale

private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

/**
 * Converts a raw TMDB date string ("yyyy-MM-dd") to a human-readable form
 * ("MMM d, yyyy", e.g. "Oct 15, 1999").
 *
 * Returns the original string unchanged if parsing fails (e.g. "Unknown").
 */
fun String.toFormattedDate(): String {
    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return runCatching { outputFormat.format(apiDateFormat.parse(this)!!) }.getOrDefault(this)
}
