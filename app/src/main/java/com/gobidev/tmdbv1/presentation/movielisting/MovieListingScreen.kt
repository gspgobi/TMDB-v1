package com.gobidev.tmdbv1.presentation.movielisting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.GenreItem
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieFilterState
import com.gobidev.tmdbv1.presentation.util.PreviewData
import com.gobidev.tmdbv1.ui.theme.TMDBTheme
import java.util.Locale

/**
 * A reusable movie listing screen that supports multiple TMDB list endpoints
 * (popular, now_playing, top_rated, upcoming) with filtering and sorting.
 *
 * The list type is encoded in the navigation route and read by [MovieListingViewModel]
 * from [SavedStateHandle]. Filters and sort are applied via a [FilterSortBottomSheet].
 *
 * @param onMovieClick Callback invoked when the user taps a movie card
 * @param viewModel Hilt-provided ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListingScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: MovieListingViewModel = hiltViewModel()
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val filterState by viewModel.filterState.collectAsState()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(viewModel.movieListType.title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        // Filter icon with active-filter badge
                        BadgedBox(
                            badge = {
                                if (filterState.activeFilterCount > 0) {
                                    Badge { Text(filterState.activeFilterCount.toString()) }
                                }
                            }
                        ) {
                            IconButton(onClick = { showBottomSheet = true }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter"
                                )
                            }
                        }

                        // Sort icon highlighted when a non-default sort is active
                        IconButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort",
                                tint = if (filterState.isSortActive) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                }
                            )
                        }
                    }
                )

                // Active filter chips strip
                if (filterState.needsDiscoverApi) {
                    ActiveFilterStrip(
                        filterState = filterState,
                        onChipClick = { showBottomSheet = true }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            MoviesList(
                movies = movies,
                onMovieClick = onMovieClick,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

    if (showBottomSheet) {
        FilterSortBottomSheet(
            currentFilters = filterState,
            onApply = { newFilters ->
                viewModel.applyFilters(newFilters)
            },
            onReset = {
                viewModel.resetFilters()
            },
            onDismiss = { showBottomSheet = false }
        )
    }
}

/**
 * A horizontally scrollable row of read-only chips summarising the active filters.
 * Tapping any chip opens the bottom sheet.
 */
@Composable
private fun ActiveFilterStrip(
    filterState: MovieFilterState,
    onChipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filterState.sortBy?.let { sort ->
            ActiveChip(label = sort.displayName, onClick = onChipClick)
        }
        if (filterState.selectedGenreIds.isNotEmpty()) {
            val genreNames = filterState.selectedGenreIds.mapNotNull { id ->
                GenreItem.ALL_GENRES.find { it.id == id }?.name
            }
            ActiveChip(
                label = if (genreNames.size == 1) genreNames.first() else "${genreNames.size} Genres",
                onClick = onChipClick
            )
        }
        if (filterState.minRating > 0f) {
            ActiveChip(label = "Min ★ %.1f".format(filterState.minRating), onClick = onChipClick)
        }
        filterState.releaseYear?.let { year ->
            ActiveChip(label = year.toString(), onClick = onChipClick)
        }
    }
}

@Composable
private fun ActiveChip(label: String, onClick: () -> Unit) {
    FilterChip(
        selected = true,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

/**
 * List of movies with pagination support.
 *
 * Handles different LoadStates:
 * - Loading: Shows progress indicator
 * - Error: Shows error message with retry button
 * - Success: Shows the list of movies
 */
@Composable
fun MoviesList(
    movies: LazyPagingItems<Movie>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(movies.itemCount) { index ->
            movies[index]?.let { movie ->
                MovieItem(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }

        // Handle loading state at the end of the list (pagination)
        item {
            when (movies.loadState.append) {
                is LoadState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> {
                    val error = (movies.loadState.append as LoadState.Error).error
                    ErrorItem(
                        message = error.message ?: "Unknown error",
                        onRetry = { movies.retry() }
                    )
                }

                else -> {}
            }
        }
    }

    // Handle initial loading state
    when (movies.loadState.refresh) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {
            val error = (movies.loadState.refresh as LoadState.Error).error
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = error.message ?: "Unknown error",
                    onRetry = { movies.retry() }
                )
            }
        }

        else -> {}
    }
}

/**
 * Individual movie item in the list.
 *
 * Displays:
 * - Poster image
 * - Title
 * - Release date
 * - Rating with vote count
 */
@Composable
fun MovieItem(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Poster Image
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Movie Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", movie.rating),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${movie.voteCount})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Error item with retry button.
 */
@Composable
fun ErrorItem(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

// ==================== Previews ====================

@Preview(showBackground = true)
@Composable
fun PreviewMovieItem() {
    TMDBTheme {
        MovieItem(
            movie = PreviewData.sampleMovie,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMovieItemList() {
    TMDBTheme {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(3) { index ->
                MovieItem(
                    movie = PreviewData.sampleMovies[index],
                    onClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorItem() {
    TMDBTheme {
        ErrorItem(
            message = "Failed to load movies. Please check your connection.",
            onRetry = {}
        )
    }
}

