package com.gobidev.tmdbv1.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.Genre
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.presentation.util.MovieDetailsUiState
import com.gobidev.tmdbv1.ui.theme.TMDBTheme

/**
 * Movie Details Screen - displays detailed information about a movie.
 *
 * Features:
 * - Backdrop and poster images
 * - Title, tagline, and overview
 * - Genres, release date, rating
 * - Runtime and status
 * - Loading and error states
 *
 * @param onBackClick Callback when back button is clicked
 * @param viewModel ViewModel provided by Hilt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MovieDetailsScreen(uiState = uiState, onBackClick = onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    uiState: MovieDetailsUiState,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is MovieDetailsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MovieDetailsUiState.Success -> {
                MovieDetailsContent(
                    movie = uiState.movie,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is MovieDetailsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * Content displaying movie details.
 */
@Composable
fun MovieDetailsContent(
    movie: MovieDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Backdrop Image
        movie.backdropUrl?.let { backdropUrl ->
            AsyncImage(
                model = backdropUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Content
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Poster
                movie.posterUrl?.let { posterUrl ->
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = movie.title,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))
                }

                // Title and basic info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tagline
                    movie.tagline?.let { tagline ->
                        if (tagline.isNotBlank()) {
                            Text(
                                text = tagline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Release Date
                    InfoRow(
                        label = "Release",
                        value = movie.releaseDate
                    )

                    // Runtime
                    movie.runtime?.let { runtime ->
                        InfoRow(
                            label = "Runtime",
                            value = "$runtime min"
                        )
                    }

                    // Rating
                    InfoRow(
                        label = "Rating",
                        value = "⭐ ${String.format("%.1f", movie.rating)} (${movie.voteCount})"
                    )

                    // Status
                    movie.status?.let { status ->
                        InfoRow(
                            label = "Status",
                            value = status
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Genres
            if (movie.genres.isNotEmpty()) {
                Text(
                    text = "Genres",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    movie.genres.forEach { genre ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(genre.name) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Overview
            if (movie.overview.isNotBlank()) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Helper composable for displaying label-value pairs.
 */
@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MovieDetailsScreenPreview() {
    val sampleMovie = MovieDetails(
        id = 1,
        title = "Sample Movie Title",
        overview = "This is a sample movie overview. It's a great movie, you should watch it.",
        posterUrl = "https://image.tmdb.org/t/p/w500/1E5baAaEse26fej7uHcjOgE2Ellis.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w1280/5a4JdoFwll5DRtKMe7JLuGQ9yJm.jpg",
        rating = 7.8,
        voteCount = 1234,
        releaseDate = "2023-10-26",
        runtime = 120,
        tagline = "An epic sample movie.",
        status = "Released",
        genres = listOf(Genre(1, "Action"), Genre(2, "Adventure"))
    )
    TMDBTheme {
        MovieDetailsScreen(
            uiState = MovieDetailsUiState.Success(sampleMovie),
            onBackClick = {}
        )
    }
}
