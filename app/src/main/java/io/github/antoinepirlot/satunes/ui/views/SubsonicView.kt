package io.github.antoinepirlot.satunes.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.github.antoinepirlot.satunes.data.states.SubsonicUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel

/**
 * @author Antoine Pirlot 20/12/2025
 */
@Composable
fun SubsonicView(
    modifier: Modifier = Modifier,
    subsonicViewModel: SubsonicViewModel,
    content: @Composable (modifier: Modifier) -> Unit
) {
    val subsonicUiState: SubsonicUiState by subsonicViewModel.uiState.collectAsState()

    if (subsonicUiState.isFetching)
        LoadingView(modifier = modifier)
    else if (subsonicViewModel.error != null)
        SubsonicErrorView(modifier = modifier, error = subsonicViewModel.error!!)
    else
        content(modifier)
}