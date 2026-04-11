package com.gobidev.tmdbv1.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.MovieListType
import com.gobidev.tmdbv1.domain.model.Person
import com.gobidev.tmdbv1.domain.model.TrendingItem
import com.gobidev.tmdbv1.domain.model.TvListType
import com.gobidev.tmdbv1.presentation.util.FeaturedHeroShimmer
import com.gobidev.tmdbv1.presentation.util.PosterShimmerRow
import com.gobidev.tmdbv1.presentation.util.PreviewData
import com.gobidev.tmdbv1.ui.theme.TMDBTheme
import java.util.Locale
import kotlinx.coroutines.delay

sealed interface HomeEvent {
    data class MovieClick(val movieId: Int) : HomeEvent
    data class ViewAllMoviesClick(val listType: MovieListType) : HomeEvent
    data class TvClick(val tvId: Int) : HomeEvent
    data class ViewAllTvClick(val listType: TvListType) : HomeEvent
    data class PersonClick(val personId: Int) : HomeEvent
    data object SearchClick : HomeEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEvent: (HomeEvent) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TMDB",
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { onEvent(HomeEvent.SearchClick) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
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
            // ── Featured Hero Banner (Trending) ───────────────────────────────
            item {
                FeaturedHeroBanner(
                    categoryState = uiState.trending,
                    onEvent = onEvent
                )
            }
            // ── Popular Movies ────────────────────────────────────────────────
            item {
                MovieCarouselSection(
                    listType = MovieListType.POPULAR,
                    categoryState = uiState.popular,
                    onEvent = onEvent
                )
            }
            // ── Popular TV Shows ──────────────────────────────────────────────
            item {
                TvCarouselSection(
                    listType = TvListType.POPULAR,
                    categoryState = uiState.popularTv,
                    onEvent = onEvent
                )
            }
            // ── Now Playing Movies ────────────────────────────────────────────
            item {
                MovieCarouselSection(
                    listType = MovieListType.NOW_PLAYING,
                    categoryState = uiState.nowPlaying,
                    onEvent = onEvent
                )
            }
            // ── TV Shows On The Air ───────────────────────────────────────────
            item {
                TvCarouselSection(
                    listType = TvListType.ON_THE_AIR,
                    categoryState = uiState.onTheAirTv,
                    onEvent = onEvent
                )
            }
            // ── Upcoming Movies ───────────────────────────────────────────────
            item {
                MovieCarouselSection(
                    listType = MovieListType.UPCOMING,
                    categoryState = uiState.upcoming,
                    onEvent = onEvent
                )
            }
            // ── Top Rated TV ──────────────────────────────────────────────────
            item {
                TvCarouselSection(
                    listType = TvListType.TOP_RATED,
                    categoryState = uiState.topRatedTv,
                    onEvent = onEvent
                )
            }
            // ── Popular People ────────────────────────────────────────────────
            item {
                PopularPeopleSection(
                    categoryState = uiState.popularPeople,
                    onEvent = onEvent
                )
            }
        }
    }
}

// ── Featured Hero Banner ──────────────────────────────────────────────────────

@Composable
private fun FeaturedHeroBanner(
    categoryState: TrendingCategoryState,
    onEvent: (HomeEvent) -> Unit
) {
    when {
        categoryState.isLoading -> FeaturedHeroShimmer()

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

        categoryState.items.isNotEmpty() -> {
            val items = categoryState.items
            val pagerState = rememberPagerState(pageCount = { items.size })

            // Auto-scroll every 4 seconds
            LaunchedEffect(pagerState) {
                while (true) {
                    delay(4000L)
                    val next = (pagerState.currentPage + 1) % items.size
                    pagerState.animateScrollToPage(next)
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    val item = items[page]
                    HeroPage(
                        item = item,
                        onClick = {
                            if (item.mediaType == "movie") onEvent(HomeEvent.MovieClick(item.id))
                            else onEvent(HomeEvent.TvClick(item.id))
                        }
                    )
                }

                // Page indicator dots
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, _ ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 8.dp else 5.dp)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else Color.White.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HeroPage(
    item: TrendingItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = item.backdropUrl ?: item.posterUrl,
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay: transparent → near-black
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.4f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // Media-type badge — top end
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = if (item.mediaType == "movie") "Movie" else "TV",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }

        // Title + rating — bottom start
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "⭐ ${String.format(Locale.getDefault(), "%.1f", item.rating)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                item.releaseDate?.let { date ->
                    Text(
                        text = "• $date",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
            }
        }
    }
}

// ── Shared Section Header ─────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(onClick = onViewAll) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "See All",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Movie Carousel Section ────────────────────────────────────────────────────

@Composable
private fun MovieCarouselSection(
    listType: MovieListType,
    categoryState: MovieCategoryState,
    onEvent: (HomeEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = listType.title,
            onViewAll = { onEvent(HomeEvent.ViewAllMoviesClick(listType)) }
        )

        when {
            categoryState.isLoading -> PosterShimmerRow()

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
                            rating = movie.rating,
                            onClick = { onEvent(HomeEvent.MovieClick(movie.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ── TV Carousel Section ───────────────────────────────────────────────────────

@Composable
private fun TvCarouselSection(
    listType: TvListType,
    categoryState: TvCategoryState,
    onEvent: (HomeEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            title = listType.title,
            onViewAll = { onEvent(HomeEvent.ViewAllTvClick(listType)) }
        )

        when {
            categoryState.isLoading -> PosterShimmerRow()

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
                            rating = show.rating,
                            onClick = { onEvent(HomeEvent.TvClick(show.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ── Popular People Section ────────────────────────────────────────────────────

@Composable
private fun PopularPeopleSection(
    categoryState: PopularPeopleCategoryState,
    onEvent: (HomeEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Popular People",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        when {
            categoryState.isLoading -> PosterShimmerRow()

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
                    items(categoryState.people) { person ->
                        PersonCard(
                            person = person,
                            onClick = { onEvent(HomeEvent.PersonClick(person.id)) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ── Person Card ───────────────────────────────────────────────────────────────

@Composable
private fun PersonCard(
    person: Person,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = person.profileUrl,
                contentDescription = person.name,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = person.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = person.knownForDepartment,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Poster Card ───────────────────────────────────────────────────────────────

@Composable
private fun PosterCard(
    posterUrl: String?,
    contentDescription: String,
    rating: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box {
            AsyncImage(
                model = posterUrl,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Rating badge — bottom-left
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(
                        color = Color.Black.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(topEnd = 6.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "⭐ ${String.format(Locale.getDefault(), "%.1f", rating)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
    }
}

// ==================== Previews ====================

@Preview(name = "PosterCard – dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPosterCard() {
    TMDBTheme {
        PosterCard(
            posterUrl = PreviewData.sampleMovie.posterUrl,
            contentDescription = PreviewData.sampleMovie.title,
            rating = PreviewData.sampleMovie.rating,
            onClick = {}
        )
    }
}

@Preview(name = "PersonCard – dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPersonCard() {
    TMDBTheme {
        PersonCard(
            person = PreviewData.samplePerson,
            onClick = {}
        )
    }
}

@Preview(name = "SectionHeader – dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSectionHeader() {
    TMDBTheme {
        SectionHeader(title = "Popular Movies", onViewAll = {})
    }
}
