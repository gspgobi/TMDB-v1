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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import com.gobidev.tmdbv1.presentation.util.MediaListShimmer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.gobidev.tmdbv1.domain.model.GenreItem
import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieFilterState
import com.gobidev.tmdbv1.presentation.components.ErrorItem
import com.gobidev.tmdbv1.presentation.components.MediaListItem
import com.gobidev.tmdbv1.presentation.util.PreviewData
import com.gobidev.tmdbv1.ui.theme.TMDBTheme

sealed interface MovieListingEvent {
    data object BackClick : MovieListingEvent
    data class MovieClick(val movieId: Int) : MovieListingEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListingScreen(
    onEvent: (MovieListingEvent) -> Unit,
    viewModel: MovieListingViewModel = hiltViewModel()
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val filterState by viewModel.filterState.collectAsState()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(filterState) {
        listState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(viewModel.screenTitle) },
                    navigationIcon = {
                        IconButton(onClick = { onEvent(MovieListingEvent.BackClick) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        if (!viewModel.isKeywordMode && !viewModel.isGenreMode) {
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
                                        contentDescription = "Filter",
                                        tint = if (filterState.activeFilterCount > 0) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        }
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
                    }
                )

                // Active filter chips strip (not shown in keyword or genre mode)
                if (!viewModel.isKeywordMode && !viewModel.isGenreMode && filterState.needsDiscoverApi) {
                    ActiveFilterStrip(
                        filterState = filterState,
                        onChipClick = { showBottomSheet = true },
                        onRemoveFilter = { viewModel.applyFilters(it) },
                        onClearAll = { viewModel.resetFilters() }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            MoviesList(
                movies = movies,
                onMovieClick = { id -> onEvent(MovieListingEvent.MovieClick(id)) },
                listState = listState,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

    if (showBottomSheet && !viewModel.isKeywordMode && !viewModel.isGenreMode) {
        MovieFilterSortBottomSheet(
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
 * A horizontally scrollable row of active filter chips.
 * Each chip has an × dismiss icon to remove that individual filter.
 * A "Clear all" button appears at the end.
 */
@Composable
private fun ActiveFilterStrip(
    filterState: MovieFilterState,
    onChipClick: () -> Unit,
    onRemoveFilter: (MovieFilterState) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filterState.sortBy?.let { sort ->
            ActiveChip(
                label = sort.displayName,
                onClick = onChipClick,
                onDismiss = { onRemoveFilter(filterState.copy(sortBy = null)) }
            )
        }
        if (filterState.selectedGenreIds.isNotEmpty()) {
            val genreNames = filterState.selectedGenreIds.mapNotNull { id ->
                GenreItem.ALL_GENRES.find { it.id == id }?.name
            }
            ActiveChip(
                label = if (genreNames.size == 1) genreNames.first() else "${genreNames.size} Genres",
                onClick = onChipClick,
                onDismiss = { onRemoveFilter(filterState.copy(selectedGenreIds = emptySet())) }
            )
        }
        if (filterState.minRating > 0f) {
            ActiveChip(
                label = "★ %.1f+".format(filterState.minRating),
                onClick = onChipClick,
                onDismiss = { onRemoveFilter(filterState.copy(minRating = 0f)) }
            )
        }
        filterState.releaseYear?.let { year ->
            ActiveChip(
                label = year.toString(),
                onClick = onChipClick,
                onDismiss = { onRemoveFilter(filterState.copy(releaseYear = null)) }
            )
        }
        TextButton(onClick = onClearAll) {
            Text(
                text = "Clear all",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ActiveChip(label: String, onClick: () -> Unit, onDismiss: () -> Unit) {
    FilterChip(
        selected = true,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove filter",
                modifier = Modifier
                    .size(FilterChipDefaults.IconSize)
                    .clickable(onClick = onDismiss)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White,
            selectedTrailingIconColor = Color.White.copy(alpha = 0.8f)
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
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = listState,
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
            MediaListShimmer(modifier = Modifier.fillMaxSize())
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
    MediaListItem(
        title = movie.title,
        posterUrl = movie.posterUrl,
        date = movie.releaseDate,
        overview = movie.overview,
        rating = movie.rating,
        voteCount = movie.voteCount,
        onClick = onClick,
        modifier = modifier
    )
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

