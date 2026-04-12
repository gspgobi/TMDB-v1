package com.gobidev.tmdbv1.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gobidev.tmdbv1.domain.model.Keyword

sealed class KeywordsUiState {
    data object Loading : KeywordsUiState()
    data class Success(val keywords: List<Keyword>) : KeywordsUiState()
    data object Empty : KeywordsUiState()
    data class Error(val message: String) : KeywordsUiState()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KeywordsSection(
    keywordsState: KeywordsUiState,
    onKeywordClick: (Keyword) -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (keywordsState) {
        is KeywordsUiState.Loading -> { /* silently skip while loading */ }

        is KeywordsUiState.Success -> {
            Column(modifier = modifier) {
                SectionTitle("Keywords")
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    keywordsState.keywords.forEach { keyword ->
                        SuggestionChip(
                            onClick = { onKeywordClick(keyword) },
                            label = { Text(keyword.name, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }
            }
        }

        is KeywordsUiState.Empty -> { /* nothing to show */ }

        is KeywordsUiState.Error -> { /* silently skip */ }
    }
}
