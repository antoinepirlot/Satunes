package io.github.antoinepirlot.satunes.ui.components.dialog.media.options

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.android.utils.utils.runIOThread
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.DownloadStatus
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicFolder
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.ui.components.dialog.options.DialogOption
import io.github.antoinepirlot.satunes.utils.getDownloadIcon
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot 23/12/2025
 */
@Composable
fun DownloadMediaOption(
    modifier: Modifier = Modifier,
    subsonicViewModel: SubsonicViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    media: SubsonicMedia
) {
    val context: Context = LocalContext.current
    val scope: CoroutineScope = LocalMainScope.current
    val snackbarHostState: SnackbarHostState = LocalSnackBarHostState.current
    val downloadStatus: DownloadStatus = media.downloadStatus

    val download: suspend () -> Unit = {
        media.download(
            context = context,
            onDataRetrieved = {
                playbackViewModel.updateMediaItems(media = media)
                dataViewModel.addToDownloadPlaylist(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    musics = if (media.isMusic()) listOf(media as SubsonicMusic)
                    else if (media.isFolder()) (media as SubsonicFolder).getAllSubsonicMusic()
                    else media.subsonicMusicCollection
                )
            }
        )
    }

    DialogOption(
        modifier = modifier,
        enabled = downloadStatus != DownloadStatus.DOWNLOADING,
        onClick = {
            if (media.canBeDownloaded())
                if (media.isAlbum())
                    subsonicViewModel.loadAlbum(
                        album = media as SubsonicAlbum,
                        onDataRetrieved = {
                            runIOThread {
                                download()
                            }
                        },
                    )
                else // Music
                    runIOThread {
                        download()
                    }
            else if (media.canDownloadBeRemoved()) media.removeDownload()
        },
        jetpackLibsIcons = getDownloadIcon(status = downloadStatus),
        text = stringResource(id = downloadStatus.stringId),
    )
}