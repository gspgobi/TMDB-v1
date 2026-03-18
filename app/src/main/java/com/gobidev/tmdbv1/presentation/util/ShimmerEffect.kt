package com.gobidev.tmdbv1.presentation.util

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.08f),
        Color.White.copy(alpha = 0.18f),
        Color.White.copy(alpha = 0.08f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 400f, translateAnim - 400f),
        end = Offset(translateAnim, translateAnim)
    )
}

/** Shimmer placeholder for a media list row (100×150dp poster + text lines). */
@Composable
fun MediaListItemShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Box(modifier = Modifier.width(100.dp).height(150.dp).background(brush))
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.8f).height(16.dp).background(brush))
                Box(modifier = Modifier.fillMaxWidth(0.5f).height(12.dp).background(brush))
                Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(brush))
                Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(brush))
                Box(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp).background(brush))
            }
        }
    }
}

/** Full-screen shimmer list for initial media list loading. */
@Composable
fun MediaListShimmer(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(6) { MediaListItemShimmer() }
    }
}

/** Shimmer placeholder for a cast/crew list row (60dp circle + 2 text lines). */
@Composable
fun CastCrewListItemShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(brush))
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp).background(brush))
                Box(modifier = Modifier.fillMaxWidth(0.5f).height(12.dp).background(brush))
            }
        }
    }
}

/** Full-screen shimmer list for cast/crew screens. */
@Composable
fun CastCrewListShimmer(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(8) { CastCrewListItemShimmer() }
    }
}

/** Shimmer placeholder for a credit card (110×150dp poster + text). */
@Composable
fun CreditCardShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Card(
        modifier = modifier.width(110.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(brush))
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(brush))
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(10.dp).background(brush))
            }
        }
    }
}

/** Horizontal row of shimmer credit cards for the "Known For" section. */
@Composable
fun CreditCarouselShimmer(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(5) { CreditCardShimmer() }
    }
}

/** Horizontal row of shimmer cast member circles (100dp each). */
@Composable
fun CastCarouselShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(5) {
            Column(
                modifier = Modifier.width(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(brush))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.8f).height(10.dp).background(brush))
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(10.dp).background(brush))
            }
        }
    }
}

/** Full-screen shimmer for details screens (backdrop + poster + text blocks). */
@Composable
fun DetailsMainShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(brush))
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.width(120.dp).height(180.dp).background(brush))
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(20.dp).background(brush))
                    Box(modifier = Modifier.fillMaxWidth(0.8f).height(14.dp).background(brush))
                    Box(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp).background(brush))
                    Box(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp).background(brush))
                    Box(modifier = Modifier.fillMaxWidth(0.5f).height(14.dp).background(brush))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth().height(14.dp).background(brush))
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(14.dp).background(brush))
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(0.9f).height(14.dp).background(brush))
        }
    }
}

/** Shimmer for person details main loading (120dp circle + text lines). */
@Composable
fun PersonMainShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Column(modifier = modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(brush))
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(20.dp).background(brush))
                Box(modifier = Modifier.fillMaxWidth(0.5f).height(14.dp).background(brush))
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(12.dp).background(brush))
                Box(modifier = Modifier.fillMaxWidth(0.6f).height(12.dp).background(brush))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxWidth().height(14.dp).background(brush))
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(14.dp).background(brush))
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(0.9f).height(14.dp).background(brush))
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp).background(brush))
    }
}

/** Shimmer for a single review card (48dp circle + text lines). */
@Composable
fun ReviewCardShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(brush))
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp).background(brush))
                    Box(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp).background(brush))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(brush))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).background(brush))
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth(0.8f).height(12.dp).background(brush))
        }
    }
}

/** Full-screen shimmer list for reviews initial loading. */
@Composable
fun ReviewListShimmer(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(5) { ReviewCardShimmer() }
    }
}

/** Horizontal row of shimmer trending cards (280×165dp each). */
@Composable
fun TrendingShimmerRow() {
    val brush = shimmerBrush()
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(4) {
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .height(165.dp)
                    .background(brush, RoundedCornerShape(12.dp))
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

/** Horizontal row of shimmer poster cards (120×180dp each). */
@Composable
fun PosterShimmerRow() {
    val brush = shimmerBrush()
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
                    .background(brush, MaterialTheme.shapes.medium)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

/** Shimmer for the profile loading state (64dp avatar + name lines). */
@Composable
fun ProfileLoadingShimmer(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Column(modifier = modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(brush))
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.width(150.dp).height(16.dp).background(brush))
                Box(modifier = Modifier.width(100.dp).height(12.dp).background(brush))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { MediaListItemShimmer() }
        }
    }
}
