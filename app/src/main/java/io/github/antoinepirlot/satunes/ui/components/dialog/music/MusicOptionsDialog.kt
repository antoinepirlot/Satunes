/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.dialog.music

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToPlaylistMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToQueueDialogOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.PlayNextMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.music.options.LikeUnlikeMusicOption
import io.github.antoinepirlot.satunes.ui.components.dialog.music.options.NavigateToMediaMusicOption
import io.github.antoinepirlot.satunes.ui.components.dialog.music.options.RemoveFromPlaylistMusicOption
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.viewmodels.PlaybackViewModel

/**
 * @author Antoine Pirlot on 30/03/2024
 */


@Composable
internal fun MusicOptionsDialog(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    playbackViewModel: PlaybackViewModel = viewModel(),
    music: Music,
    playlist: Playlist? = null,
    onDismissRequest: () -> Unit,
) {
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

                if (playlist != null) {
                    RemoveFromPlaylistMusicOption(
                        music = music,
                        playlist = playlist,
                        onFinished = onDismissRequest
                    )
                }

                val isPlaybackLoaded: Boolean = playbackViewModel.isLoaded

                if (isPlaybackLoaded) {
                    if (music != musicPlaying) {
                        PlayNextMediaOption(mediaImpl = music, onFinished = onDismissRequest)
                        if (!playbackViewModel.isMusicInQueue(music = music)) {
                            AddToQueueDialogOption(mediaImpl = music, onFinished = onDismissRequest)
                        }
                    }
                }

                /**
                 * Redirections
                 */
                NavigateToMediaMusicOption(navController = navController, mediaImpl = music.album)
                NavigateToMediaMusicOption(navController = navController, mediaImpl = music.artist)
                NavigateToMediaMusicOption(navController = navController, mediaImpl = music.genre)
                NavigateToMediaMusicOption(navController = navController, mediaImpl = music.folder)
            }
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = { /* Nothing */ }
    )
}