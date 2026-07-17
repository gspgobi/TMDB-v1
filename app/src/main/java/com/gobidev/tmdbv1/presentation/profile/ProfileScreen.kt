package com.gobidev.tmdbv1.presentation.profile

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import com.gobidev.tmdbv1.presentation.util.MediaListShimmer
import com.gobidev.tmdbv1.presentation.util.ProfileLoadingShimmer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.work.WorkInfo
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.data.local.db.MediaType
import com.gobidev.tmdbv1.domain.model.UserAccount
import androidx.compose.ui.tooling.preview.Preview
import com.gobidev.tmdbv1.presentation.components.ErrorItem
import com.gobidev.tmdbv1.presentation.movielisting.MovieItem
import com.gobidev.tmdbv1.presentation.tvlisting.TvShowItem
import com.gobidev.tmdbv1.presentation.util.PreviewData
import com.gobidev.tmdbv1.ui.theme.TMDBTheme
import kotlinx.coroutines.launch

sealed interface ProfileEvent {
    data object LoginClick : ProfileEvent
    data class MovieClick(val movieId: Int) : ProfileEvent
    data class TvClick(val tvId: Int) : ProfileEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEvent: (ProfileEvent) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val releaseCheckWorkInfo by viewModel.releaseCheckWorkInfo.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LifecycleResumeEffect(Unit) {
        if (viewModel.sessionManager.isLoggedIn && viewModel.uiState.value is ProfileUiState.LoggedOut) {
            viewModel.loadAccount()
        }
        onPauseOrDispose { }
    }

    LaunchedEffect(releaseCheckWorkInfo?.state, releaseCheckWorkInfo?.id) {
        val info = releaseCheckWorkInfo ?: return@LaunchedEffect
        val message = when (info.state) {
            WorkInfo.State.RUNNING, WorkInfo.State.ENQUEUED -> "Checking watchlist for releases…"
            WorkInfo.State.SUCCEEDED -> {
                val count = info.outputData.getInt("new_releases_count", 0)
                if (count > 0) "$count new release(s) found" else "No new releases"
            }
            WorkInfo.State.FAILED -> "Couldn't check for releases"
            else -> null
        }
        message?.let { coroutineScope.launch { snackbarHostState.showSnackbar(it) } }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (uiState is ProfileUiState.LoggedIn) {
                        IconButton(onClick = { viewModel.checkForReleasesNow() }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Check for releases",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        TextButton(onClick = { viewModel.logout() }) {
                            Text(
                                "Sign Out",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is ProfileUiState.LoggedOut -> {
                LoggedOutContent(
                    onLoginClick = { onEvent(ProfileEvent.LoginClick) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is ProfileUiState.Loading -> {
                ProfileLoadingShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is ProfileUiState.LoggedIn -> {
                LoggedInContent(
                    account = state.account,
                    onMovieClick = { id -> onEvent(ProfileEvent.MovieClick(id)) },
                    onTvClick = { id -> onEvent(ProfileEvent.TvClick(id)) },
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAccount() }) { Text("Retry") }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoggedOutContent(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sign in to access your profile",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Sign In to TMDB")
            }
        }
    }
}

@Composable
private fun LoggedInContent(
    account: UserAccount,
    onMovieClick: (Int) -> Unit,
    onTvClick: (Int) -> Unit,
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    var selectedMediaType by rememberSaveable { mutableStateOf(MediaType.MOVIE) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        // Account header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (account.avatarUrl != null) {
                AsyncImage(
                    model = account.avatarUrl,
                    contentDescription = account.username,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = account.username.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = account.username, style = MaterialTheme.typography.titleMedium)
                if (account.name.isNotBlank()) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Media type toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedMediaType == MediaType.MOVIE,
                onClick = { selectedMediaType = MediaType.MOVIE },
                label = { Text("Movies") }
            )
            FilterChip(
                selected = selectedMediaType == MediaType.TV,
                onClick = { selectedMediaType = MediaType.TV },
                label = { Text("TV") }
            )
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Favorites") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Watchlist") }
            )
        }

        // Tab content
        val emptyMessage = if (selectedTab == 0) "No favorites yet" else "Watchlist is empty"

        when (selectedMediaType) {
            MediaType.MOVIE -> {
                val items = if (selectedTab == 0) {
                    viewModel.favorites.collectAsLazyPagingItems()
                } else {
                    viewModel.watchlist.collectAsLazyPagingItems()
                }
                MediaPagingList(
                    items = items,
                    emptyMessage = emptyMessage,
                    itemContent = { movie -> MovieItem(movie = movie, onClick = { onMovieClick(movie.id) }) }
                )
            }

            MediaType.TV -> {
                val items = if (selectedTab == 0) {
                    viewModel.tvFavorites.collectAsLazyPagingItems()
                } else {
                    viewModel.tvWatchlist.collectAsLazyPagingItems()
                }
                MediaPagingList(
                    items = items,
                    emptyMessage = emptyMessage,
                    itemContent = { show -> TvShowItem(show = show, onClick = { onTvClick(show.id) }) }
                )
            }
        }
    }
}

/**
 * Renders a [LazyPagingItems] list with loading/empty/error states.
 * A refresh error only blocks the whole screen when there's nothing cached to show
 * ([LazyPagingItems.itemCount] == 0) — with a [RemoteMediator]-backed source (TV feeds),
 * Room can still hold valid cached items while a background sync fails.
 */
@Composable
private fun <T : Any> MediaPagingList(
    items: LazyPagingItems<T>,
    emptyMessage: String,
    itemContent: @Composable (T) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (items.loadState.refresh is LoadState.Loading && items.itemCount == 0) {
            MediaListShimmer(modifier = Modifier.fillMaxSize())
        } else if (items.loadState.refresh is LoadState.Error && items.itemCount == 0) {
            val error = (items.loadState.refresh as LoadState.Error).error
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = error.message ?: "Unknown error",
                    onRetry = { items.retry() }
                )
            }
        } else if (items.itemCount == 0 && items.loadState.refresh !is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items.itemCount) { index ->
                    items[index]?.let { itemContent(it) }
                }

                item {
                    when (items.loadState.append) {
                        is LoadState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }

                        is LoadState.Error -> {
                            val error = (items.loadState.append as LoadState.Error).error
                            ErrorItem(
                                message = error.message ?: "Unknown error",
                                onRetry = { items.retry() }
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}

// ==================== Previews ====================

@Preview(name = "Profile – logged out", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewLoggedOutContent() {
    TMDBTheme {
        LoggedOutContent(
            onLoginClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
