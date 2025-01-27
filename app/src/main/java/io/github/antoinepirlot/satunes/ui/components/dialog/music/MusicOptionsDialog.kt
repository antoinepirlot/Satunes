/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.dialog.music

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToPlaylistMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToQueueDialogOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.PlayNextMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.RemoveFromQueueOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.ShareMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.music.options.LikeUnlikeMusicOption
import io.github.antoinepirlot.satunes.ui.components.dialog.music.options.NavigateToMediaMusicOption
import io.github.antoinepirlot.satunes.ui.components.dialog.music.options.RemoveFromPlaylistMusicOption

/**
 * @author Antoine Pirlot on 30/03/2024
 */


@Composable
internal fun MusicOptionsDialog(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    music: Music,
    onDismissRequest: () -> Unit,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val currentMediaImpl: MediaImpl? = satunesUiState.currentMediaImpl

    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = SatunesIcons.MUSIC.imageVector,
                contentDescription = "Music Options Icon"
            )
        },
        title = {
            NormalText(text = music.title)
        },
        text = {
            Column {
                val musicPlaying: Music? = playbackViewModel.musicPlaying
                LikeUnlikeMusicOption(music = music)
                AddToPlaylistMediaOption(mediaImpl = music, onFinished = onDismissRequest)

                if (currentMediaImpl is Playlist) {
                    RemoveFromPlaylistMusicOption(
                        music = music,
                        playlist = currentMediaImpl,
                        onFinished = onDismissRequest
                    )
                }

                val isPlaybackLoaded: Boolean = playbackViewModel.isLoaded

                if (isPlaybackLoaded) {
                    if (music != musicPlaying) {
                        if (music != playbackViewModel.getNextMusic()) {
                            PlayNextMediaOption(
                                mediaImpl = music,
                                onDismissRequest = onDismissRequest
                            )
                        }
                        if (!playbackViewModel.isMusicInQueue(music = music)) {
                            AddToQueueDialogOption(
                                mediaImpl = music,
                                onDismissRequest = onDismissRequest
                            )
                        } else {
                            RemoveFromQueueOption(
                                mediaImpl = music,
                                onDismissRequest = onDismissRequest
                            )
                        }
                    }
                }

                /**
                 * Share
                 */
                ShareMediaOption(media = music)

                /**
                 * Redirections
                 */
                if (currentMediaImpl != music.album)
                    NavigateToMediaMusicOption(mediaImpl = music.album)
                if (currentMediaImpl != music.artist)
                    NavigateToMediaMusicOption(mediaImpl = music.artist)
                if (currentMediaImpl != music.genre)
                    NavigateToMediaMusicOption(mediaImpl = music.genre)
                if (currentMediaImpl != music.folder)
                    NavigateToMediaMusicOption(mediaImpl = music.folder)
            }
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = { /* Nothing */ }
    )
}