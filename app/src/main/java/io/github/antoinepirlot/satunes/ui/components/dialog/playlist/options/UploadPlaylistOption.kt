package io.github.antoinepirlot.satunes.ui.components.dialog.playlist.options

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.ui.components.dialog.options.DialogOption
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot 26/12/2025
 */

@Composable
fun UploadPlaylistOption(
    modifier: Modifier = Modifier,
    subsonicViewModel: SubsonicViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    playlist: Playlist,
) {
    if (playlist.isSubsonic()) throw IllegalArgumentException("Can't show option for subsonic playlist.")
    val scope: CoroutineScope = LocalMainScope.current
    val snackbarHostState: SnackbarHostState = LocalSnackBarHostState.current
    var isLoading: Boolean by rememberSaveable { mutableStateOf(value = false) }

    DialogOption(
        modifier = modifier,
        onClick = {
            isLoading = true
            subsonicViewModel.createPlaylist(
                name = playlist.title,
                onDataRetrieved = {
                    dataViewModel.removePlaylist(
                        scope = scope,
                        snackBarHostState = snackbarHostState,
                        playlist = playlist
                    )
                    dataViewModel.replace(old = playlist, new = it)
                },
                onFinished = { isLoading = false }
            )
        },
        jetpackLibsIcons = JetpackLibsIcons.UPLOAD,
        text = stringResource(id = R.string.upload_text),
        isLoading = isLoading,
    )
}