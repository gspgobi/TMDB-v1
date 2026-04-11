package com.gobidev.tmdbv1.presentation.tvlisting

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import com.gobidev.tmdbv1.presentation.util.MediaListShimmer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.TvFilterState
import com.gobidev.tmdbv1.domain.model.TvGenreItem
import com.gobidev.tmdbv1.domain.model.TvShow
import com.gobidev.tmdbv1.presentation.movielisting.ErrorItem
import com.gobidev.tmdbv1.presentation.util.PreviewData
import com.gobidev.tmdbv1.ui.theme.TMDBTheme
import java.util.Locale

sealed interface TvListingEvent {
    data object BackClick : TvListingEvent
    data class TvClick(val tvId: Int) : TvListingEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvListingScreen(
    onEvent: (TvListingEvent) -> Unit,
    viewModel: TvListingViewModel = hiltViewModel()
) {
    val tvShows = viewModel.tvShows.collectAsLazyPagingItems()
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
                        IconButton(onClick = { onEvent(TvListingEvent.BackClick) }) {
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

                if (!viewModel.isKeywordMode && !viewModel.isGenreMode && filterState.needsDiscoverApi) {
                    TvActiveFilterStrip(
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
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tvShows.itemCount) { index ->
                    tvShows[index]?.let { show ->
                        TvShowItem(show = show, onClick = { onEvent(TvListingEvent.TvClick(show.id)) })
                    }
                }

                item {
                    when (tvShows.loadState.append) {
                        is LoadState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                        is LoadState.Error -> {
                            val error = (tvShows.loadState.append as LoadState.Error).error
                            ErrorItem(
                                message = error.message ?: "Unknown error",
                                onRetry = { tvShows.retry() }
                            )
                        }
                        else -> {}
                    }
                }
            }

            when (tvShows.loadState.refresh) {
                is LoadState.Loading -> {
                    MediaListShimmer(modifier = Modifier.fillMaxSize())
                }
                is LoadState.Error -> {
                    val error = (tvShows.loadState.refresh as LoadState.Error).error
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ErrorItem(
                            message = error.message ?: "Unknown error",
                            onRetry = { tvShows.retry() }
                        )
                    }
                }
                else -> {}
            }
        }
    }

    if (showBottomSheet && !viewModel.isKeywordMode && !viewModel.isGenreMode) {
        TvFilterSortBottomSheet(
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

@Composable
private fun TvActiveFilterStrip(
    filterState: TvFilterState,
    onChipClick: () -> Unit,
    onRemoveFilter: (TvFilterState) -> Unit,
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
            TvActiveChip(
                label = sort.displayName,
                onClick = onChipClick,
                onDismiss = { onRemoveFilter(filterState.copy(sortBy = null)) }
            )
        }
        if (filterState.selectedGenreIds.isNotEmpty()) {
            val genreNames = filterState.selectedGenreIds.mapNotNull { id ->
                TvGenreItem.ALL_GENRES.find { it.id == id }?.name
            }
            TvActiveChip(
                label = if (genreNames.size == 1) genreNames.first() else "${genreNames.size} Genres",
                onClick = onChipClick,
                onDismiss = { onRemoveFilter(filterState.copy(selectedGenreIds = emptySet())) }
            )
        }
        if (filterState.minRating > 0f) {
            TvActiveChip(
                label = "★ %.1f+".format(filterState.minRating),
                onClick = onChipClick,
                onDismiss = { onRemoveFilter(filterState.copy(minRating = 0f)) }
            )
        }
        filterState.firstAirYear?.let { year ->
            TvActiveChip(
                label = year.toString(),
                onClick = onChipClick,
                onDismiss = { onRemoveFilter(filterState.copy(firstAirYear = null)) }
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
private fun TvActiveChip(label: String, onClick: () -> Unit, onDismiss: () -> Unit) {
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

@Composable
fun TvShowItem(
    show: TvShow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            AsyncImage(
                model = show.posterUrl,
                contentDescription = show.name,
                modifier = Modifier.width(100.dp).height(150.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = show.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = show.firstAirDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = show.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", show.rating),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${show.voteCount})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ==================== Previews ====================

@Preview(name = "TvShowItem", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTvShowItem() {
    TMDBTheme {
        TvShowItem(
            show = PreviewData.sampleTvShows.first(),
            onClick = {}
        )
    }
}

@Preview(name = "TvShowItem – list", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTvShowItemList() {
    TMDBTheme {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(PreviewData.sampleTvShows) { show ->
                TvShowItem(show = show, onClick = {})
            }
        }
    }
}
