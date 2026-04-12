package com.gobidev.tmdbv1.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.MovieImage
import com.gobidev.tmdbv1.presentation.util.CastCarouselShimmer
import kotlinx.coroutines.launch

sealed class ImagesUiState {
    data object Loading : ImagesUiState()
    data class Success(val backdrops: List<MovieImage>, val posters: List<MovieImage>) : ImagesUiState()
    data object Empty : ImagesUiState()
    data class Error(val message: String) : ImagesUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesSection(
    imagesState: ImagesUiState,
    modifier: Modifier = Modifier
) {
    when (imagesState) {
        is ImagesUiState.Loading -> {
            SectionTitle("Images")
            Spacer(modifier = Modifier.height(12.dp))
            CastCarouselShimmer()
        }

        is ImagesUiState.Success -> {
            if (imagesState.backdrops.isNotEmpty() || imagesState.posters.isNotEmpty()) {
                var selectedTab by remember { mutableIntStateOf(0) }
                var viewerVisible by remember { mutableStateOf(false) }
                var viewerInitialIndex by remember { mutableIntStateOf(0) }

                val tabs = listOf(
                    "Backdrops (${imagesState.backdrops.size})",
                    "Posters (${imagesState.posters.size})"
                )

                SectionTitle("Images")
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryTabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val images = if (selectedTab == 0) imagesState.backdrops else imagesState.posters
                if (images.isNotEmpty()) {
                    LazyRow(
                        modifier = modifier,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        items(images.size) { index ->
                            if (selectedTab == 0) {
                                BackdropThumbnail(image = images[index], onClick = {
                                    viewerInitialIndex = index
                                    viewerVisible = true
                                })
                            } else {
                                PosterThumbnail(image = images[index], onClick = {
                                    viewerInitialIndex = index
                                    viewerVisible = true
                                })
                            }
                        }
                    }

                    if (viewerVisible) {
                        ImageViewerDialog(
                            images = images,
                            initialIndex = viewerInitialIndex,
                            onDismiss = { viewerVisible = false }
                        )
                    }
                } else {
                    Text(
                        text = if (selectedTab == 0) "No backdrops available" else "No posters available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        is ImagesUiState.Empty -> { /* nothing to show */ }

        is ImagesUiState.Error -> { /* silently skip */ }
    }
}

@Composable
private fun BackdropThumbnail(
    image: MovieImage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dynamicWidth = (135.dp * image.aspectRatio.toFloat()).coerceIn(135.dp, 320.dp)
    Card(
        modifier = modifier
            .width(dynamicWidth)
            .height(135.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = image.url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun PosterThumbnail(
    image: MovieImage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(90.dp)
            .height(135.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = image.url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ImageViewerDialog(
    images: List<MovieImage>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { images.size }
    val coroutineScope = rememberCoroutineScope()

    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Snap (not animate) on page change — instant reset feels right when swiping between pages
    LaunchedEffect(pagerState.currentPage) {
        scale.snapTo(1f)
        offsetX.snapTo(0f)
        offsetY.snapTo(0f)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .onSizeChanged { containerSize = it }
                .pointerInput(coroutineScope) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        do {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val zoom = event.calculateZoom()
                            val pan = event.calculatePan()
                            if (event.changes.size > 1) {
                                // Multi-touch: zoom + pan. Consume to block pager swipe.
                                val newScale = (scale.value * zoom).coerceIn(1f, 3f)
                                if (newScale > 1f) {
                                    val maxX = containerSize.width * (newScale - 1) / 2f
                                    val maxY = containerSize.height * (newScale - 1) / 2f
                                    coroutineScope.launch {
                                        scale.snapTo(newScale)
                                        offsetX.snapTo((offsetX.value + pan.x).coerceIn(-maxX, maxX))
                                        offsetY.snapTo((offsetY.value + pan.y).coerceIn(-maxY, maxY))
                                    }
                                } else {
                                    // Pinched back to 1× — reset pan cleanly
                                    coroutineScope.launch {
                                        scale.snapTo(1f)
                                        offsetX.snapTo(0f)
                                        offsetY.snapTo(0f)
                                    }
                                }
                                event.changes.forEach { it.consume() }
                            } else if (scale.value > 1f) {
                                // Single-touch pan while zoomed in. Don't consume — lets close
                                // button taps and double-tap gestures still reach their handlers.
                                val maxX = containerSize.width * (scale.value - 1) / 2f
                                val maxY = containerSize.height * (scale.value - 1) / 2f
                                coroutineScope.launch {
                                    offsetX.snapTo((offsetX.value + pan.x).coerceIn(-maxX, maxX))
                                    offsetY.snapTo((offsetY.value + pan.y).coerceIn(-maxY, maxY))
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = scale.value == 1f
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    coroutineScope.launch {
                                        if (scale.value > 1f) {
                                            // Animate scale + pan back to neutral in parallel
                                            launch { scale.animateTo(1f) }
                                            launch { offsetX.animateTo(0f) }
                                            launch { offsetY.animateTo(0f) }
                                        } else {
                                            scale.animateTo(2.5f)
                                        }
                                    }
                                }
                            )
                        }
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                            translationX = offsetX.value
                            translationY = offsetY.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = images[page].url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Text(
                text = "${pagerState.currentPage + 1} / ${images.size}",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}
