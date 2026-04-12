package com.gobidev.tmdbv1.presentation.moviedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import com.gobidev.tmdbv1.presentation.util.CastCrewListShimmer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.CrewMember
import com.gobidev.tmdbv1.presentation.components.CastCrewRow
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
sealed interface FullCastCrewEvent {
    data object BackClick : FullCastCrewEvent
    data class PersonClick(val personId: Int) : FullCastCrewEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullCastCrewScreen(
    onEvent: (FullCastCrewEvent) -> Unit,
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
                    IconButton(onClick = { onEvent(FullCastCrewEvent.BackClick) }) {
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
                CastCrewListShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is FullCastCrewUiState.Success -> {
                FullCastCrewContent(
                    cast = state.cast,
                    crew = state.crew,
                    onPersonClick = { personId -> onEvent(FullCastCrewEvent.PersonClick(personId)) },
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
    onPersonClick: (Int) -> Unit = {},
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
            CastCrewRow(
                name = castMember.name,
                role = castMember.character,
                profileUrl = castMember.profileUrl,
                onClick = { onPersonClick(castMember.id) }
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
            CastCrewRow(
                name = crewMember.name,
                role = crewMember.job,
                department = crewMember.department,
                profileUrl = crewMember.profileUrl,
                onClick = { onPersonClick(crewMember.id) }
            )
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
fun PreviewCastCrewRowCast() {
    TMDBTheme {
        CastCrewRow(
            name = "Edward Norton",
            role = "The Narrator",
            profileUrl = "https://image.tmdb.org/t/p/w185/5XBzD5WuTyVQZeS4VI25z2moMeY.jpg"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCastCrewRowCrew() {
    TMDBTheme {
        CastCrewRow(
            name = "David Fincher",
            role = "Director",
            department = "Directing",
            profileUrl = "https://image.tmdb.org/t/p/w185/tpEczFclQZeKAiCeKZZ0adRvtfz.jpg"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCastCrewRowNoImage() {
    TMDBTheme {
        CastCrewRow(
            name = "Jim Uhls",
            role = "Screenplay",
            department = "Writing",
            profileUrl = null
        )
    }
}
