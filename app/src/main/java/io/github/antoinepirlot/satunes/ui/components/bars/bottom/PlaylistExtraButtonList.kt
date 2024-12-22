package io.github.antoinepirlot.satunes.ui.components.bars.bottom

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.MediaSelectionViewModel
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton
import io.github.antoinepirlot.satunes.ui.components.dialog.MediaSelectionDialog
import kotlinx.coroutines.CoroutineScope

@Composable
internal fun PlaylistExtraButtonList(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    mediaSelectionViewModel: MediaSelectionViewModel = viewModel(),
    playlist: Playlist,
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackbarHostState: SnackbarHostState = LocalSnackBarHostState.current

    var openAddMusicsDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExtraButton(
            icon = SatunesIcons.EXPORT,
            onClick = { dataViewModel.exportPlaylist(playlist = playlist) }
        )
        ExtraButton(
            icon = SatunesIcons.ADD,
            onClick = { openAddMusicsDialog = true },
        )
    }

    if (openAddMusicsDialog) {
        MediaSelectionDialog(
            onDismissRequest = { openAddMusicsDialog = false },
            onConfirm = {
                dataViewModel.insertMusicsToPlaylist(
                    scope = scope,
                    snackBarHostState = snackbarHostState,
                    musics = mediaSelectionViewModel.getCheckedMusics(),
                    playlist = playlist
                )
                openAddMusicsDialog = false
            },
            mediaImplCollection = dataViewModel.getMusicSet(),
            icon = SatunesIcons.PLAYLIST_ADD,
            playlistTitle = playlist.title
        )
    }
}