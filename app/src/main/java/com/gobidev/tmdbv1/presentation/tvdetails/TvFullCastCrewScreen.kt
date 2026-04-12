package com.gobidev.tmdbv1.presentation.tvdetails

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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import com.gobidev.tmdbv1.domain.model.CastMember
import com.gobidev.tmdbv1.domain.model.CrewMember
import com.gobidev.tmdbv1.presentation.components.CastCrewRow
import com.gobidev.tmdbv1.presentation.util.PreviewData
import com.gobidev.tmdbv1.ui.theme.TMDBTheme

sealed interface TvFullCastCrewEvent {
    data object BackClick : TvFullCastCrewEvent
    data class PersonClick(val personId: Int) : TvFullCastCrewEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvFullCastCrewScreen(
    onEvent: (TvFullCastCrewEvent) -> Unit,
    viewModel: TvFullCastCrewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cast & Crew - ${viewModel.tvName}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(TvFullCastCrewEvent.BackClick) }) {
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
            is TvFullCastUiState.Loading -> {
                CastCrewListShimmer(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                )
            }

            is TvFullCastUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.cast.isNotEmpty()) {
                        item {
                            Text(
                                text = "Cast",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(state.cast) { member ->
                            CastCrewRow(
                                name = member.name,
                                role = member.character,
                                profileUrl = member.profileUrl,
                                onClick = { onEvent(TvFullCastCrewEvent.PersonClick(member.id)) }
                            )
                        }
                    }

                    if (state.crew.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Crew",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(state.crew) { member ->
                            CastCrewRow(
                                name = member.name,
                                role = member.job,
                                profileUrl = member.profileUrl,
                                department = member.department,
                                onClick = { onEvent(TvFullCastCrewEvent.PersonClick(member.id)) }
                            )
                        }
                    }
                }
            }

            is TvFullCastUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry() }) { Text("Retry") }
                    }
                }
            }
        }
    }
}

// ==================== Previews ====================

@Preview(name = "TvCastCrewRow – cast", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTvCastCrewRowCast() {
    val member = PreviewData.sampleCastMembers.first()
    TMDBTheme {
        CastCrewRow(name = member.name, role = member.character, profileUrl = member.profileUrl)
    }
}

@Preview(name = "TvCastCrewRow – crew", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTvCastCrewRowCrew() {
    val member = PreviewData.sampleCrewMembers.first()
    TMDBTheme {
        CastCrewRow(name = member.name, role = member.job, profileUrl = member.profileUrl, department = member.department)
    }
}
