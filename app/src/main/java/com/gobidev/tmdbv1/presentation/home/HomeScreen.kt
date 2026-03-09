package com.gobidev.tmdbv1.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.model.TvListType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onViewAllClick: (MovieListType) -> Unit,
    onTvClick: (Int) -> Unit,
    onViewAllTvClick: (TvListType) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        topBar = {
            TopAppBar(
                title = { Text("TMDB") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // ── Movies ───────────────────────────────────────────────────────
            item {
                MovieCarouselSection(
                    title = MovieListType.POPULAR.title,
                    categoryState = uiState.popular,
                    onMovieClick = onMovieClick,
                    onViewAllClick = { onViewAllClick(MovieListType.POPULAR) }
                )
            }
            // ── TV Series ────────────────────────────────────────────────────
            item {
                TvCarouselSection(
                    title = TvListType.POPULAR.title,
                    categoryState = uiState.popularTv,
                    onTvClick = onTvClick,
                    onViewAllClick = { onViewAllTvClick(TvListType.POPULAR) }
                )
            }
            // ── Movies ───────────────────────────────────────────────────────
            item {
                MovieCarouselSection(
                    title = MovieListType.NOW_PLAYING.title,
                    categoryState = uiState.nowPlaying,
                    onMovieClick = onMovieClick,
                    onViewAllClick = { onViewAllClick(MovieListType.NOW_PLAYING) }
                )
            }
            // ── TV Series ────────────────────────────────────────────────────
            item {
                TvCarouselSection(
                    title = TvListType.ON_THE_AIR.title,
                    categoryState = uiState.onTheAirTv,
                    onTvClick = onTvClick,
                    onViewAllClick = { onViewAllTvClick(TvListType.ON_THE_AIR) }
                )
            }
            // ── Movies ───────────────────────────────────────────────────────
            item {
                MovieCarouselSection(
                    title = MovieListType.UPCOMING.title,
                    categoryState = uiState.upcoming,
                    onMovieClick = onMovieClick,
                    onViewAllClick = { onViewAllClick(MovieListType.UPCOMING) }
                )
            }
            // ── TV Series ────────────────────────────────────────────────────
            item {
                TvCarouselSection(
                    title = TvListType.TOP_RATED.title,
                    categoryState = uiState.topRatedTv,
                    onTvClick = onTvClick,
                    onViewAllClick = { onViewAllTvClick(TvListType.TOP_RATED) }
                )
            }
        }
    }
}

@Composable
private fun MovieCarouselSection(
    title: String,
    categoryState: MovieCategoryState,
    onMovieClick: (Int) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onViewAllClick) { Text("View All") }
        }

        when {
            categoryState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            categoryState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryState.error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categoryState.movies) { movie ->
                        PosterCard(
                            posterUrl = movie.posterUrl,
                            contentDescription = movie.title,
                            onClick = { onMovieClick(movie.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TvCarouselSection(
    title: String,
    categoryState: TvCategoryState,
    onTvClick: (Int) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onViewAllClick) { Text("View All") }
        }

        when {
            categoryState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            categoryState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryState.error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categoryState.shows) { show ->
                        PosterCard(
                            posterUrl = show.posterUrl,
                            contentDescription = show.name,
                            onClick = { onTvClick(show.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PosterCard(
    posterUrl: String?,
    contentDescription: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
