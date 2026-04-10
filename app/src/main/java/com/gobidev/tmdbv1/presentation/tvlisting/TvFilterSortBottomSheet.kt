package com.gobidev.tmdbv1.presentation.tvlisting

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gobidev.tmdbv1.domain.model.MovieSortOption
import com.gobidev.tmdbv1.domain.model.TvFilterState
import com.gobidev.tmdbv1.domain.model.TvGenreItem
import java.util.Calendar
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TvFilterSortBottomSheet(
    currentFilters: TvFilterState,
    onApply: (TvFilterState) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var draftSort by rememberSaveable { mutableStateOf(currentFilters.sortBy) }
    var draftGenres by rememberSaveable { mutableStateOf(currentFilters.selectedGenreIds) }
    var draftMinRating by rememberSaveable { mutableStateOf(currentFilters.minRating) }
    var draftYear by rememberSaveable { mutableStateOf(currentFilters.firstAirYear) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            // ── Sort By ──────────────────────────────────────────────────────────
            TvSectionHeader("Sort By")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MovieSortOption.entries.forEach { option ->
                    FilterChip(
                        selected = draftSort == option,
                        onClick = {
                            draftSort = if (draftSort == option) null else option
                        },
                        label = { Text(option.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ── Genres ───────────────────────────────────────────────────────────
            TvSectionHeader("Genres")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TvGenreItem.ALL_GENRES.forEach { genre ->
                    FilterChip(
                        selected = genre.id in draftGenres,
                        onClick = {
                            draftGenres = if (genre.id in draftGenres) {
                                draftGenres - genre.id
                            } else {
                                draftGenres + genre.id
                            }
                        },
                        label = { Text(genre.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ── Min Rating ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Min Rating",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = if (draftMinRating > 0f) "%.1f ★".format(draftMinRating) else "Any",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = draftMinRating,
                onValueChange = { draftMinRating = it },
                valueRange = 0f..10f,
                steps = 19,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ── First Air Year ───────────────────────────────────────────────────
            TvSectionHeader("First Air Year")
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val years = listOf(null) + (currentYear downTo currentYear - 9).toList()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                years.forEach { year ->
                    FilterChip(
                        selected = draftYear == year,
                        onClick = { draftYear = year },
                        label = { Text(year?.toString() ?: "Any") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Action Buttons ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        draftSort = null
                        draftGenres = emptySet()
                        draftMinRating = 0f
                        draftYear = null
                        onReset()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = {
                        onApply(
                            TvFilterState(
                                sortBy = draftSort,
                                selectedGenreIds = draftGenres,
                                minRating = (draftMinRating * 2).roundToInt() / 2f,
                                firstAirYear = draftYear
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
private fun TvSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
