package io.github.antoinepirlot.satunes.ui.components.bars.bottom

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton

/**
 * Extra Button list to show on scaffold.
 *
 * On click, it will load the playback with the media list loaded.
 *
 * @param modifier the [Modifier].
 * @param playbackViewModel the [PlaybackViewModel] initialized by default.
 */
@Composable
internal fun ExtraButtonList(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val mediaImplCollection: Collection<MediaImpl> = dataUiState.mediaImplList

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExtraButton(
            icon = SatunesIcons.PLAY,
            onClick = {
                playbackViewModel.loadMusicFromMedias(medias = mediaImplCollection)
                openMedia(
                    playbackViewModel = playbackViewModel,
                    navController = navController
                )
            }
        )
        ExtraButton(
            icon = SatunesIcons.SHUFFLE,
            onClick = {
                playbackViewModel.loadMusicFromMedias(
                    medias = mediaImplCollection,
                    shuffleMode = true
                )
                openMedia(
                    playbackViewModel = playbackViewModel,
                    navController = navController
                )
            }
        )
    }
}