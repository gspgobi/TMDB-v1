package com.gobidev.tmdbv1.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.annotation.DrawableRes
import androidx.compose.ui.platform.LocalUriHandler
import com.gobidev.tmdbv1.R
import com.gobidev.tmdbv1.domain.model.ExternalIds

sealed class ExternalIdsUiState {
    data object Loading : ExternalIdsUiState()
    data class Success(val externalIds: ExternalIds) : ExternalIdsUiState()
    data object Empty : ExternalIdsUiState()
    data class Error(val message: String) : ExternalIdsUiState()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExternalIdsSection(
    state: ExternalIdsUiState,
    modifier: Modifier = Modifier,
    isPersonProfile: Boolean = false
) {
    if (state !is ExternalIdsUiState.Success) return

    val links = buildExternalLinks(state.externalIds, isPersonProfile)
    if (links.isEmpty()) return

    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier) {
        SectionTitle("External Links")
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            links.forEach { link ->
                AssistChip(
                    onClick = { uriHandler.openUri(link.url) },
                    label = { Text(link.label, style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(link.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                )
            }
        }
    }
}

private data class ExternalLink(
    val label: String,
    val url: String,
    @DrawableRes val iconRes: Int
)

private fun buildExternalLinks(ids: ExternalIds, isPersonProfile: Boolean): List<ExternalLink> = buildList {
    ids.imdbId?.let {
        val imdbUrl = if (isPersonProfile) "https://www.imdb.com/name/$it"
                      else "https://www.imdb.com/title/$it"
        add(ExternalLink("IMDb", imdbUrl, R.drawable.ic_imdb))
    }
    ids.tvdbId?.let {
        add(ExternalLink("TVDB", "https://thetvdb.com/?id=$it&tab=series", R.drawable.ic_tvdb))
    }
    ids.facebookId?.let {
        add(ExternalLink("Facebook", "https://www.facebook.com/$it", R.drawable.ic_facebook))
    }
    ids.instagramId?.let {
        add(ExternalLink("Instagram", "https://www.instagram.com/$it", R.drawable.ic_instagram))
    }
    ids.twitterId?.let {
        add(ExternalLink("X / Twitter", "https://twitter.com/$it", R.drawable.ic_twitter))
    }
    ids.wikidataId?.let {
        add(ExternalLink("Wikidata", "https://www.wikidata.org/wiki/$it", R.drawable.ic_wikidata))
    }
}
