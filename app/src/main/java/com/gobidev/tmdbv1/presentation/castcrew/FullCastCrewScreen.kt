package com.gobidev.tmdbv1.presentation.castcrew

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.CrewMember
import com.gobidev.tmdbv1.presentation.util.PreviewData
import com.gobidev.tmdbv1.ui.theme.TMDBTheme

/**
 * Full Cast & Crew Screen - displays complete cast and crew for a movie.
 *
 * Features:
 * - Complete cast list with profile images
 * - Complete crew list grouped by department
 * - Loading and error states
 * - Search/filter capability (future enhancement)
 *
 * @param onBackClick Callback when back button is clicked
 * @param viewModel ViewModel provided by Hilt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullCastCrewScreen(
    onBackClick: () -> Unit,
    viewModel: FullCastCrewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cast & Crew - ${viewModel.movieTitle}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
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
            is FullCastCrewUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is FullCastCrewUiState.Success -> {
                FullCastCrewContent(
                    cast = state.cast,
                    crew = state.crew,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is FullCastCrewUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Content displaying full cast and crew lists.
 */
@Composable
fun FullCastCrewContent(
    cast: List<CastMember>,
    crew: List<CrewMember>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Cast Section Header
        item {
            Text(
                text = "Cast (${cast.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Cast Members List
        items(cast) { castMember ->
            CastCrewListItem(
                name = castMember.name,
                role = castMember.character,
                profileUrl = castMember.profileUrl
            )
        }

        // Spacing between sections
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Crew Section Header
        item {
            Text(
                text = "Crew (${crew.size})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Crew Members List
        items(crew) { crewMember ->
            CastCrewListItem(
                name = crewMember.name,
                role = crewMember.job,
                department = crewMember.department,
                profileUrl = crewMember.profileUrl
            )
        }
    }
}

/**
 * List item for cast or crew member.
 *
 * Displays profile image, name, role, and optionally department.
 */
@Composable
fun CastCrewListItem(
    name: String,
    role: String,
    profileUrl: String?,
    department: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            if (profileUrl != null) {
                AsyncImage(
                    model = profileUrl,
                    contentDescription = name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder for members without profile images
                Surface(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name and Role
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Department (for crew members)
                department?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ==================== Previews ====================

@Preview(showBackground = true)
@Composable
fun PreviewFullCastCrewContent() {
    TMDBTheme {
        FullCastCrewContent(
            cast = PreviewData.sampleCastMembers,
            crew = PreviewData.sampleCrewMembers
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCastCrewListItemCast() {
    TMDBTheme {
        CastCrewListItem(
            name = "Edward Norton",
            role = "The Narrator",
            profileUrl = "https://image.tmdb.org/t/p/w185/5XBzD5WuTyVQZeS4VI25z2moMeY.jpg"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCastCrewListItemCrew() {
    TMDBTheme {
        CastCrewListItem(
            name = "David Fincher",
            role = "Director",
            department = "Directing",
            profileUrl = "https://image.tmdb.org/t/p/w185/tpEczFclQZeKAiCeKZZ0adRvtfz.jpg"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCastCrewListItemNoImage() {
    TMDBTheme {
        CastCrewListItem(
            name = "Jim Uhls",
            role = "Screenplay",
            department = "Writing",
            profileUrl = null
        )
    }
}
