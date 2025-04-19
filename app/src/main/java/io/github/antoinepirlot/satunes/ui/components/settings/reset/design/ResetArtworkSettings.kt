package io.github.antoinepirlot.satunes.ui.components.settings.reset.design

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.ui.components.settings.reset.ResetSettings
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot 19/04/2025
 */
@Composable
fun ResetArtworkSettings(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel()
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    ResetSettings(
        modifier = modifier,
        text = stringResource(id = R.string.artwork_sub_settings_title),
        onClick = {
            dataViewModel.resetArtworkSettings(
                scope = scope,
                snackBarHostState = snackBarHostState
            )
        }
    )
}

@Preview
@Composable
private fun ResetArtworkSettingsPreview() {
    ResetArtworkSettings()
}