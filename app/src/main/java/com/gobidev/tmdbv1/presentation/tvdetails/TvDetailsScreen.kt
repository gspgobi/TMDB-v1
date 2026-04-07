package com.gobidev.tmdbv1.presentation.tvdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.Episode
import com.gobidev.tmdbv1.domain.model.Season
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.presentation.components.ExternalIdsSection
import com.gobidev.tmdbv1.presentation.moviedetails.CastSection
import com.gobidev.tmdbv1.presentation.moviedetails.ExternalIdsUiState
import com.gobidev.tmdbv1.presentation.moviedetails.ImagesSection
import com.gobidev.tmdbv1.presentation.moviedetails.InfoRow
import com.gobidev.tmdbv1.presentation.moviedetails.MovieCastUiState
import com.gobidev.tmdbv1.presentation.moviedetails.MovieImagesUiState
import com.gobidev.tmdbv1.presentation.moviedetails.KeywordsSection
import com.gobidev.tmdbv1.presentation.moviedetails.MovieKeywordsUiState
import com.gobidev.tmdbv1.presentation.moviedetails.MovieVideosUiState
import com.gobidev.tmdbv1.presentation.moviedetails.VideosSection
import com.gobidev.tmdbv1.presentation.tvdetails.TvImagesUiState
import com.gobidev.tmdbv1.presentation.util.DetailsMainShimmer
import java.util.Locale

sealed interface TvDetailsEvent {
    data object BackClick : TvDetailsEvent
    data class ViewFullCastClick(val tvId: Int, val tvName: String) : TvDetailsEvent
    data class CastMemberClick(val personId: Int) : TvDetailsEvent
    data class RecommendationClick(val tvId: Int) : TvDetailsEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvDetailsScreen(
    onEvent: (TvDetailsEvent) -> Unit,
    viewModel: TvDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val castState by viewModel.castState.collectAsStateWithLifecycle()
    val selectedSeasonIndex by viewModel.selectedSeasonIndex.collectAsStateWithLifecycle()
    val episodesState by viewModel.episodesState.collectAsStateWithLifecycle()
    val externalIdsState by viewModel.externalIdsState.collectAsStateWithLifecycle()
    val imagesState by viewModel.imagesState.collectAsStateWithLifecycle()
    val recommendationsState by viewModel.recommendationsState.collectAsStateWithLifecycle()
    val videosState by viewModel.videosState.collectAsStateWithLifecycle()
    val keywordsState by viewModel.keywordsState.collectAsStateWithLifecycle()

    val mappedImagesState: MovieImagesUiState = when (val s = imagesState) {
        is TvImagesUiState.Loading -> MovieImagesUiState.Loading
        is TvImagesUiState.Success -> MovieImagesUiState.Success(s.backdrops, s.posters)
        is TvImagesUiState.Empty -> MovieImagesUiState.Empty
        is TvImagesUiState.Error -> MovieImagesUiState.Error(s.message)
    }

    val mappedCastState: MovieCastUiState = when (val cs = castState) {
        is TvCastUiState.Loading -> MovieCastUiState.Loading
        is TvCastUiState.Success -> MovieCastUiState.Success(cs.cast)
        is TvCastUiState.Error -> MovieCastUiState.Error(cs.message)
    }

    val mappedVideosState: MovieVideosUiState = when (val v = videosState) {
        is TvVideosUiState.Loading -> MovieVideosUiState.Loading
        is TvVideosUiState.Success -> MovieVideosUiState.Success(v.videos)
        is TvVideosUiState.Empty -> MovieVideosUiState.Empty
        is TvVideosUiState.Error -> MovieVideosUiState.Error(v.message)
    }

    val mappedKeywordsState: MovieKeywordsUiState = when (val k = keywordsState) {
        is TvKeywordsUiState.Loading -> MovieKeywordsUiState.Loading
        is TvKeywordsUiState.Success -> MovieKeywordsUiState.Success(k.keywords)
        is TvKeywordsUiState.Empty -> MovieKeywordsUiState.Empty
        is TvKeywordsUiState.Error -> MovieKeywordsUiState.Error(k.message)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TV Details") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(TvDetailsEvent.BackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is TvDetailsUiState.Loading -> {
                DetailsMainShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is TvDetailsUiState.Success -> {
                TvDetailsContent(
                    tvShow = state.tvShow,
                    castState = mappedCastState,
                    selectedSeasonIndex = selectedSeasonIndex,
                    episodesState = episodesState,
                    externalIdsState = externalIdsState,
                    imagesState = mappedImagesState,
                    videosState = mappedVideosState,
                    keywordsState = mappedKeywordsState,
                    recommendationsState = recommendationsState,
                    onSeasonSelect = { viewModel.selectSeason(it) },
                    onLoadMore = { viewModel.loadMoreEpisodes() },
                    onEvent = onEvent,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is TvDetailsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TvDetailsContent(
    tvShow: TvShowDetails,
    castState: MovieCastUiState,
    selectedSeasonIndex: Int,
    episodesState: EpisodesUiState,
    externalIdsState: ExternalIdsUiState,
    imagesState: MovieImagesUiState,
    videosState: MovieVideosUiState,
    keywordsState: MovieKeywordsUiState,
    recommendationsState: TvRecommendationsUiState,
    onSeasonSelect: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onEvent: (TvDetailsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        tvShow.backdropUrl?.let { backdropUrl ->
            AsyncImage(
                model = backdropUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                tvShow.posterUrl?.let { posterUrl ->
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = tvShow.name,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tvShow.name, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    tvShow.tagline?.let { tagline ->
                        if (tagline.isNotBlank()) {
                            Text(
                                text = tagline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    InfoRow(label = "First Air", value = tvShow.firstAirDate)
                    InfoRow(
                        label = "Rating",
                        value = "⭐ ${
                            String.format(
                                Locale.getDefault(),
                                "%.1f",
                                tvShow.rating
                            )
                        } (${tvShow.voteCount})"
                    )
                    tvShow.status?.let { InfoRow(label = "Status", value = it) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (tvShow.genres.isNotEmpty()) {
                Text(text = "Genres", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tvShow.genres.forEach { genre ->
                        SuggestionChip(onClick = { }, label = { Text(genre.name) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (tvShow.overview.isNotBlank()) {
                Text(text = "Overview", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = tvShow.overview, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (tvShow.seasons.isNotEmpty()) {
                SeasonsSection(
                    seasons = tvShow.seasons,
                    selectedIndex = selectedSeasonIndex,
                    episodesState = episodesState,
                    onSeasonSelect = onSeasonSelect,
                    onLoadMore = onLoadMore
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            CastSection(
                castState = castState,
                onViewFullCastClick = {
                    onEvent(
                        TvDetailsEvent.ViewFullCastClick(
                            tvShow.id,
                            tvShow.name
                        )
                    )
                },
                onCastMemberClick = { id -> onEvent(TvDetailsEvent.CastMemberClick(id)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExternalIdsSection(state = externalIdsState)

            Spacer(modifier = Modifier.height(16.dp))

            VideosSection(videosState = videosState)

            Spacer(modifier = Modifier.height(16.dp))

            ImagesSection(imagesState = imagesState)

            Spacer(modifier = Modifier.height(16.dp))

            TvRecommendationsSection(
                recommendationsState = recommendationsState,
                onShowClick = { id -> onEvent(TvDetailsEvent.RecommendationClick(id)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            KeywordsSection(keywordsState = keywordsState)
        }
    }
}

@Composable
private fun TvRecommendationsSection(
    recommendationsState: TvRecommendationsUiState,
    onShowClick: (tvId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (recommendationsState) {
            is TvRecommendationsUiState.Loading -> {
                Text("Recommendations", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                com.gobidev.tmdbv1.presentation.util.CastCarouselShimmer()
            }

            is TvRecommendationsUiState.Success -> {
                Text("Recommendations", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(recommendationsState.shows) { show ->
                        TvRecommendationCard(show = show, onClick = { onShowClick(show.id) })
                    }
                }
            }

            is TvRecommendationsUiState.Empty -> { /* nothing to show */ }

            is TvRecommendationsUiState.Error -> {
                Text("Recommendations", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Unable to load recommendations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TvRecommendationCard(
    show: com.gobidev.tmdbv1.domain.model.TvShow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            AsyncImage(
                model = show.posterUrl,
                contentDescription = show.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = show.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "⭐ ${String.format(Locale.getDefault(), "%.1f", show.rating)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SeasonsSection(
    seasons: List<Season>,
    selectedIndex: Int,
    episodesState: EpisodesUiState,
    onSeasonSelect: (Int) -> Unit,
    onLoadMore: () -> Unit
) {
    val selected = seasons[selectedIndex]

    Text(text = "Seasons & Episodes", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(10.dp))

    // Season selector chips
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 8.dp)
    ) {
        itemsIndexed(seasons) { index, season ->
            FilterChip(
                selected = index == selectedIndex,
                onClick = { onSeasonSelect(index) },
                label = { Text(season.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Selected season card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            if (selected.posterUrl != null) {
                AsyncImage(
                    model = selected.posterUrl,
                    contentDescription = selected.name,
                    modifier = Modifier
                        .size(width = 80.dp, height = 120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(width = 80.dp, height = 120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "S${selected.seasonNumber}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = selected.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selected.episodeCount} Episodes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (selected.airDate.isNotBlank()) {
                        Text(
                            "·", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selected.airDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (selected.overview.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selected.overview,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    // Episode list
    Spacer(modifier = Modifier.height(12.dp))

    when (val state = episodesState) {
        is EpisodesUiState.Idle, is EpisodesUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }

        is EpisodesUiState.Error -> {
            Text(
                text = state.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        is EpisodesUiState.Success -> {
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                state.episodes.forEachIndexed { index, episode ->
                    EpisodeItem(episode = episode)
                    if (index < state.episodes.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }

            if (state.episodes.size < state.totalCount) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onLoadMore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Load More Episodes (${state.totalCount - state.episodes.size} remaining)",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeItem(episode: Episode) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Still image (16:9)
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 68.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            if (episode.stillUrl != null) {
                AsyncImage(
                    model = episode.stillUrl,
                    contentDescription = episode.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Ep ${episode.episodeNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Episode number · runtime
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ep ${episode.episodeNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                episode.runtime?.let { mins ->
                    Text(
                        "·", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${mins}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (episode.airDate.isNotBlank()) {
                    Text(
                        "·", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = episode.airDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = episode.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (episode.overview.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = episode.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
