package com.gobidev.tmdbv1.presentation.reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import com.gobidev.tmdbv1.presentation.util.ReviewListShimmer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.ui.tooling.preview.Preview
import com.gobidev.tmdbv1.domain.model.Review
import com.gobidev.tmdbv1.presentation.components.ExpandableReviewCard
import com.gobidev.tmdbv1.presentation.util.PreviewData
import com.gobidev.tmdbv1.ui.theme.TMDBTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieReviewsScreen(
    onBackClick: () -> Unit,
    viewModel: MovieReviewsViewModel = hiltViewModel()
) {
    val reviews = viewModel.reviews.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reviews - ${viewModel.movieTitle}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        ReviewsList(
            reviews = reviews,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ReviewsList(
    reviews: LazyPagingItems<Review>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reviews.itemCount) { index ->
            reviews[index]?.let { review ->
                ExpandableReviewCard(review = review)
            }
        }

        // Loading state
        item {
            when (reviews.loadState.append) {
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
                    val error = (reviews.loadState.append as LoadState.Error).error
                    Text(
                        text = error.message ?: "Error loading reviews",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {}
            }
        }
    }

    // Initial loading
    when (reviews.loadState.refresh) {
        is LoadState.Loading -> {
            ReviewListShimmer(modifier = Modifier.fillMaxSize())
        }

        is LoadState.Error -> {
            val error = (reviews.loadState.refresh as LoadState.Error).error
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = error.message ?: "Error loading reviews",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        else -> {}
    }
}

// ==================== Previews ====================

@Preview(name = "ReviewCard – collapsed", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewReviewCardCollapsed() {
    TMDBTheme {
        ExpandableReviewCard(review = PreviewData.sampleReview)
    }
}

@Preview(name = "ReviewCard – long content", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewReviewCardLong() {
    TMDBTheme {
        ExpandableReviewCard(
            review = PreviewData.sampleReview.copy(
                content = "In my top 5 of all time favourite movies. Great story line and a movie you can watch over and over again. " +
                    "The performances by Edward Norton and Brad Pitt are absolutely outstanding. " +
                    "David Fincher has created a masterpiece that challenges our understanding of identity and consumerism. " +
                    "The twist at the end is legendary and completely reframes everything you thought you knew. " +
                    "The cinematography, score, and pacing are all perfect. This film will stand the test of time as one of cinema's greatest achievements."
            )
        )
    }
}