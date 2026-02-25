package com.gobidev.tmdbv1.presentation.details

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.presentation.util.PreviewData
import com.gobidev.tmdbv1.ui.theme.TMDBTheme
import java.util.Locale

/**
 * Movie Details Screen - displays detailed information about a movie.
 *
 * Features:
 * - Backdrop and poster images
 * - Title, tagline, and overview
 * - Genres, release date, rating
 * - Runtime and status
 * - Cast members
 * - Loading and error states
 *
 * @param onBackClick Callback when back button is clicked
 * @param onViewFullCastClick Callback when "View Full Cast & Crew" button is clicked
 * @param viewModel ViewModel provided by Hilt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    onBackClick: () -> Unit,
    onViewFullCastClick: (movieId: Int, movieTitle: String) -> Unit,
    onViewAllReviewsClick: (movieId: Int, movieTitle: String) -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val castState by viewModel.castState.collectAsStateWithLifecycle()
    val reviewState by viewModel.reviewState.collectAsStateWithLifecycle()


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
        when (val state = uiState) {
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
                    movie = state.movie,
                    castState = castState,
                    reviewState = reviewState,
                    onViewFullCastClick = {
                        onViewFullCastClick(state.movie.id, state.movie.title)
                    },
                    onViewAllReviewsClick = {
                        onViewAllReviewsClick(
                            state.movie.id,
                            state.movie.title
                        )
                    },
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
}

/**
 * Content displaying movie details.
 */
@Composable
fun MovieDetailsContent(
    movie: MovieDetails,
    castState: MovieCastUiState,
    reviewState: MovieReviewUiState,
    onViewFullCastClick: () -> Unit,
    onViewAllReviewsClick: () -> Unit,
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
                                fontStyle = FontStyle.Italic
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
                        value = "⭐ ${
                            String.format(
                                Locale.getDefault(),
                                "%.1f",
                                movie.rating
                            )
                        } (${movie.voteCount})"
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
                FlowRow(
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Cast Section
            CastSection(
                castState = castState,
                onViewFullCastClick = onViewFullCastClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Review Section
            ReviewSection(
                reviewState = reviewState,
                onViewAllReviewsClick = onViewAllReviewsClick
            )
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

/**
 * Cast section displaying movie cast members.
 * Handles different states: Loading, Success, Error, and Idle.
 */
@Composable
fun CastSection(
    castState: MovieCastUiState,
    onViewFullCastClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (castState) {
            is MovieCastUiState.Loading -> {
                Text(
                    text = "Cast",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is MovieCastUiState.Success -> {
                if (castState.cast.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cast",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(onClick = onViewFullCastClick) {
                            Text("Full Cast & Crew")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(castState.cast) { castMember ->
                            CastMemberItem(castMember = castMember)
                        }
                    }
                }
            }

            is MovieCastUiState.Error -> {
                Text(
                    text = "Cast",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Unable to load cast information",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Individual cast member card with profile image, name, and character.
 */
@Composable
fun CastMemberItem(
    castMember: CastMember,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        if (castMember.profileUrl != null) {
            AsyncImage(
                model = castMember.profileUrl,
                contentDescription = castMember.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder for cast members without profile images
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = castMember.name.take(1),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cast Member Name
        Text(
            text = castMember.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        // Character Name
        Text(
            text = castMember.character,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ReviewSection(
    reviewState: MovieReviewUiState,
    onViewAllReviewsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (reviewState) {
            is MovieReviewUiState.Loading -> {
                Text("Reviews", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }

            is MovieReviewUiState.Success -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reviews", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onViewAllReviewsClick) {
                        Text("Read All Reviews")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                ReviewCard(review = reviewState.review)
            }

            is MovieReviewUiState.NoReviews -> {
                Text("Reviews", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "No reviews yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is MovieReviewUiState.Error -> {
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Unable to load reviews",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    isExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author row
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (review.authorAvatarUrl != null) {
                    AsyncImage(
                        model = review.authorAvatarUrl,
                        contentDescription = review.author,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.author,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    review.rating?.let { rating ->
                        Text(
                            text = "⭐ ${String.format(Locale.getDefault(), "%.1f", rating)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Review content
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==================== Previews ====================

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMovieDetailsScreen() {
    TMDBTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Movie Details") },
                    navigationIcon = {
                        IconButton(onClick = { }) {
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
            MovieDetailsContent(
                movie = PreviewData.sampleMovieDetails,
                castState = MovieCastUiState.Success(PreviewData.sampleCastMembers),
                reviewState = MovieReviewUiState.Success(PreviewData.sampleReview),
                onViewFullCastClick = {},
                onViewAllReviewsClick = {},
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMovieDetailsContent() {
    TMDBTheme {
        MovieDetailsContent(
            movie = PreviewData.sampleMovieDetails,
            castState = MovieCastUiState.Success(PreviewData.sampleCastMembers),
            reviewState = MovieReviewUiState.Success(PreviewData.sampleReview),
            onViewFullCastClick = {},
            onViewAllReviewsClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMovieDetailsContentLoading() {
    TMDBTheme {
        MovieDetailsContent(
            movie = PreviewData.sampleMovieDetails,
            castState = MovieCastUiState.Loading,
            reviewState = MovieReviewUiState.Loading,
            onViewFullCastClick = {},
            onViewAllReviewsClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMovieDetailsContentError() {
    TMDBTheme {
        MovieDetailsContent(
            movie = PreviewData.sampleMovieDetails,
            castState = MovieCastUiState.Error("Failed to load cast"),
            reviewState = MovieReviewUiState.Error("Failed to load reviews"),
            onViewFullCastClick = {},
            onViewAllReviewsClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCastMemberItem() {
    TMDBTheme {
        CastMemberItem(castMember = PreviewData.sampleCastMembers[0])
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCastMemberItemNoImage() {
    TMDBTheme {
        CastMemberItem(castMember = PreviewData.sampleCastMembers[3])
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewCastSection() {
    TMDBTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                CastSection(
                    castState = MovieCastUiState.Success(PreviewData.sampleCastMembers),
                    onViewFullCastClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun PreviewCastSectionLoading() {
    TMDBTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                CastSection(
                    castState = MovieCastUiState.Loading,
                    onViewFullCastClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInfoRow() {
    TMDBTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "Release", value = "1999-10-15")
                InfoRow(label = "Runtime", value = "139 min")
                InfoRow(label = "Rating", value = "⭐ 8.4 (26280)")
            }
        }
    }
}
