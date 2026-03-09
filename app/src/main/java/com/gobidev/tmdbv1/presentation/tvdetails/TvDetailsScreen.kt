package com.gobidev.tmdbv1.presentation.tvdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.TvShowDetails
import com.gobidev.tmdbv1.presentation.details.CastSection
import com.gobidev.tmdbv1.presentation.details.InfoRow
import com.gobidev.tmdbv1.presentation.details.MovieCastUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvDetailsScreen(
    onBackClick: () -> Unit,
    onViewFullCastClick: (tvId: Int, tvName: String) -> Unit,
    viewModel: TvDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val castState by viewModel.castState.collectAsStateWithLifecycle()

    // Map TvCastUiState to MovieCastUiState for reuse of CastSection composable
    val mappedCastState: MovieCastUiState = when (val cs = castState) {
        is TvCastUiState.Loading -> MovieCastUiState.Loading
        is TvCastUiState.Success -> MovieCastUiState.Success(cs.cast)
        is TvCastUiState.Error -> MovieCastUiState.Error(cs.message)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TV Details") },
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
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is TvDetailsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is TvDetailsUiState.Success -> {
                TvDetailsContent(
                    tvShow = state.tvShow,
                    castState = mappedCastState,
                    onViewFullCastClick = { onViewFullCastClick(state.tvShow.id, state.tvShow.name) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is TvDetailsUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
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
    onViewFullCastClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        tvShow.backdropUrl?.let { backdropUrl ->
            AsyncImage(
                model = backdropUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                tvShow.posterUrl?.let { posterUrl ->
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = tvShow.name,
                        modifier = Modifier.width(120.dp).height(180.dp),
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
                    InfoRow(label = "Seasons", value = tvShow.numberOfSeasons.toString())
                    InfoRow(label = "Episodes", value = tvShow.numberOfEpisodes.toString())
                    InfoRow(
                        label = "Rating",
                        value = "⭐ ${String.format(Locale.getDefault(), "%.1f", tvShow.rating)} (${tvShow.voteCount})"
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

            CastSection(
                castState = castState,
                onViewFullCastClick = onViewFullCastClick
            )
        }
    }
}
