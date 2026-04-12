package com.gobidev.tmdbv1.presentation.moviedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue

import com.gobidev.tmdbv1.presentation.components.CastMemberItem
import com.gobidev.tmdbv1.presentation.components.CastSection
import com.gobidev.tmdbv1.presentation.components.CastUiState
import com.gobidev.tmdbv1.presentation.components.ExternalIdsSection
import com.gobidev.tmdbv1.presentation.components.ExternalIdsUiState
import com.gobidev.tmdbv1.presentation.components.ImagesSection
import com.gobidev.tmdbv1.presentation.components.ImagesUiState
import com.gobidev.tmdbv1.presentation.components.KeywordsSection
import com.gobidev.tmdbv1.presentation.components.KeywordsUiState
import com.gobidev.tmdbv1.presentation.components.VideosSection
import com.gobidev.tmdbv1.presentation.components.VideosUiState
import com.gobidev.tmdbv1.presentation.util.CastCarouselShimmer
import com.gobidev.tmdbv1.presentation.util.DetailsMainShimmer
import com.gobidev.tmdbv1.presentation.util.ReviewCardShimmer
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import com.gobidev.tmdbv1.domain.model.Movie
import com.gobidev.tmdbv1.domain.model.MovieBelongsToCollection
import com.gobidev.tmdbv1.domain.model.MovieDetails
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.presentation.components.InfoRow
import com.gobidev.tmdbv1.presentation.components.SectionTitle
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
 */
sealed interface MovieDetailsEvent {
    data object BackClick : MovieDetailsEvent
    data class ViewFullCastClick(val movieId: Int, val movieTitle: String) : MovieDetailsEvent
    data class ViewAllReviewsClick(val movieId: Int, val movieTitle: String) : MovieDetailsEvent
    data class CastMemberClick(val personId: Int) : MovieDetailsEvent
    data class CollectionClick(val collectionId: Int, val collectionName: String) : MovieDetailsEvent
    data class RecommendationClick(val movieId: Int) : MovieDetailsEvent
    data class KeywordClick(val keywordId: Int, val keywordName: String) : MovieDetailsEvent
    data class GenreClick(val genreId: Int, val genreName: String) : MovieDetailsEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    onEvent: (MovieDetailsEvent) -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val castState by viewModel.castState.collectAsStateWithLifecycle()
    val reviewState by viewModel.reviewState.collectAsStateWithLifecycle()
    val recommendationsState by viewModel.recommendationsState.collectAsStateWithLifecycle()
    val externalIdsState by viewModel.externalIdsState.collectAsStateWithLifecycle()
    val imagesState by viewModel.imagesState.collectAsStateWithLifecycle()
    val videosState by viewModel.videosState.collectAsStateWithLifecycle()
    val keywordsState by viewModel.keywordsState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Movie Details") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(MovieDetailsEvent.BackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is MovieDetailsUiState.Loading -> {
                DetailsMainShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is MovieDetailsUiState.Success -> {
                MovieDetailsContent(
                    movie = state.movie,
                    castState = castState,
                    reviewState = reviewState,
                    recommendationsState = recommendationsState,
                    externalIdsState = externalIdsState,
                    imagesState = imagesState,
                    videosState = videosState,
                    keywordsState = keywordsState,
                    onEvent = onEvent,
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
    castState: CastUiState,
    reviewState: MovieReviewUiState,
    recommendationsState: MovieRecommendationsUiState = MovieRecommendationsUiState.Loading,
    externalIdsState: ExternalIdsUiState = ExternalIdsUiState.Loading,
    imagesState: ImagesUiState = ImagesUiState.Loading,
    videosState: VideosUiState = VideosUiState.Loading,
    keywordsState: KeywordsUiState = KeywordsUiState.Loading,
    onEvent: (MovieDetailsEvent) -> Unit,
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

            // Overview
            if (movie.overview.isNotBlank()) {
                SectionTitle("Overview")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Collection Section
            movie.belongsToCollection?.let { collection ->
                BelongsToCollectionSection(
                    collection = collection,
                    onClick = { onEvent(MovieDetailsEvent.CollectionClick(collection.id, collection.name)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Cast Section
            CastSection(
                castState = castState,
                onViewFullCastClick = { onEvent(MovieDetailsEvent.ViewFullCastClick(movie.id, movie.title)) },
                onCastMemberClick = { id -> onEvent(MovieDetailsEvent.CastMemberClick(id)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Review Section
            ReviewSection(
                reviewState = reviewState,
                onViewAllReviewsClick = { onEvent(MovieDetailsEvent.ViewAllReviewsClick(movie.id, movie.title)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Genres
            if (movie.genres.isNotEmpty()) {
                SectionTitle("Genres")
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    movie.genres.forEach { genre ->
                        SuggestionChip(
                            onClick = { onEvent(MovieDetailsEvent.GenreClick(genre.id, genre.name)) },
                            label = { Text(genre.name) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Videos Section
            VideosSection(videosState = videosState)

            Spacer(modifier = Modifier.height(16.dp))

            // Images Section
            ImagesSection(imagesState = imagesState)

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendations Section
            RecommendationsSection(
                recommendationsState = recommendationsState,
                onMovieClick = { movieId -> onEvent(MovieDetailsEvent.RecommendationClick(movieId)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Keywords Section
            KeywordsSection(
                keywordsState = keywordsState,
                onKeywordClick = { keyword -> onEvent(MovieDetailsEvent.KeywordClick(keyword.id, keyword.name)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // External Links Section
            ExternalIdsSection(state = externalIdsState)
        }
    }
}

@Composable
private fun BelongsToCollectionSection(
    collection: MovieBelongsToCollection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SectionTitle("Part of a Collection")
    Spacer(modifier = Modifier.height(12.dp))
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = collection.backdropUrl ?: collection.posterUrl,
                contentDescription = collection.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
            )
            Text(
                text = collection.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp)
            )
        }
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
                SectionTitle("Reviews")
                Spacer(modifier = Modifier.height(12.dp))
                ReviewCardShimmer()
            }

            is MovieReviewUiState.Success -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle("Reviews")
                    TextButton(onClick = onViewAllReviewsClick) {
                        Text("Read All Reviews")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                ReviewCard(review = reviewState.review)
            }

            is MovieReviewUiState.NoReviews -> {
                SectionTitle("Reviews")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No reviews yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is MovieReviewUiState.Error -> {
                SectionTitle("Reviews")
                Spacer(modifier = Modifier.height(12.dp))
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

@Composable
fun RecommendationsSection(
    recommendationsState: MovieRecommendationsUiState,
    onMovieClick: (movieId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (recommendationsState) {
            is MovieRecommendationsUiState.Loading -> {
                SectionTitle("Recommendations")
                Spacer(modifier = Modifier.height(12.dp))
                CastCarouselShimmer()
            }

            is MovieRecommendationsUiState.Success -> {
                SectionTitle("Recommendations")
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(recommendationsState.movies) { movie ->
                        RecommendationCard(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) }
                        )
                    }
                }
            }

            is MovieRecommendationsUiState.Empty -> { /* nothing to show */ }

            is MovieRecommendationsUiState.Error -> {
                SectionTitle("Recommendations")
                Spacer(modifier = Modifier.height(12.dp))
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
private fun RecommendationCard(
    movie: Movie,
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
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        movie.rating.let { rating ->
            Text(
                text = "⭐ ${String.format(Locale.getDefault(), "%.1f", rating)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== Previews ====================

private val allSuccessStates = Triple(
    MovieRecommendationsUiState.Success(PreviewData.sampleMovies),
    ExternalIdsUiState.Success(PreviewData.sampleExternalIds),
    ImagesUiState.Success(PreviewData.sampleImages, PreviewData.samplePosters)
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Movie Details — Dark (Pixel 5)",
    showSystemUi = true,
    device = Devices.PIXEL_5,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
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
                castState = CastUiState.Success(PreviewData.sampleCastMembers),
                reviewState = MovieReviewUiState.Success(PreviewData.sampleReview),
                recommendationsState = allSuccessStates.first,
                externalIdsState = allSuccessStates.second,
                imagesState = allSuccessStates.third,
                videosState = VideosUiState.Success(PreviewData.sampleVideos),
                keywordsState = KeywordsUiState.Success(PreviewData.sampleKeywords),
                onEvent = {},
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Preview(name = "Movie Details Content — Full", showBackground = true)
@Composable
fun PreviewMovieDetailsContent() {
    TMDBTheme {
        MovieDetailsContent(
            movie = PreviewData.sampleMovieDetails,
            castState = CastUiState.Success(PreviewData.sampleCastMembers),
            reviewState = MovieReviewUiState.Success(PreviewData.sampleReview),
            recommendationsState = allSuccessStates.first,
            externalIdsState = allSuccessStates.second,
            imagesState = allSuccessStates.third,
            videosState = VideosUiState.Success(PreviewData.sampleVideos),
            keywordsState = KeywordsUiState.Success(PreviewData.sampleKeywords),
            onEvent = {},
        )
    }
}

@Preview(name = "Movie Details Content — Loading", showBackground = true)
@Composable
fun PreviewMovieDetailsContentLoading() {
    TMDBTheme {
        MovieDetailsContent(
            movie = PreviewData.sampleMovieDetails,
            castState = CastUiState.Loading,
            reviewState = MovieReviewUiState.Loading,
            recommendationsState = MovieRecommendationsUiState.Loading,
            externalIdsState = ExternalIdsUiState.Loading,
            imagesState = ImagesUiState.Loading,
            videosState = VideosUiState.Loading,
            keywordsState = KeywordsUiState.Loading,
            onEvent = {},
        )
    }
}

@Preview(name = "Movie Details Content — Error", showBackground = true)
@Composable
fun PreviewMovieDetailsContentError() {
    TMDBTheme {
        MovieDetailsContent(
            movie = PreviewData.sampleMovieDetails,
            castState = CastUiState.Error("Failed to load cast"),
            reviewState = MovieReviewUiState.Error("Failed to load reviews"),
            onEvent = {},
        )
    }
}

@Preview(name = "Cast Member — With Image", showBackground = true)
@Composable
fun PreviewCastMemberItem() {
    TMDBTheme {
        CastMemberItem(castMember = PreviewData.sampleCastMembers[0])
    }
}

@Preview(name = "Cast Member — No Image", showBackground = true)
@Composable
fun PreviewCastMemberItemNoImage() {
    TMDBTheme {
        CastMemberItem(castMember = PreviewData.sampleCastMembers[3])
    }
}

@Preview(name = "Info Row", showBackground = true)
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
