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

package io.github.antoinepirlot.satunes.ui.components.dialog.album

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToPlaylistMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToQueueDialogOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.PlayNextMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.music.options.NavigateToMediaMusicOption
import io.github.antoinepirlot.satunes.ui.components.images.AlbumArtwork
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText

/**
 * @author Antoine Pirlot on 07/07/2024
 */


@Composable
internal fun AlbumOptionsDialog(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    album: Album,
    onDismissRequest: () -> Unit,

) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = { /* Nothing */ },
        icon = {
            AlbumArtwork(
                modifier = Modifier.size(100.dp),
                mediaImpl = album
            )
        },
        title = {
            NormalText(text = album.title)
        },
        text = {
            Column {
                val playbackController: PlaybackController = PlaybackController.getInstance()
                val isPlaybackLoaded: Boolean by rememberSaveable { playbackController.isLoaded }

                /**
                 * PlaylistDB
                 */
                AddToPlaylistMediaOption(mediaImpl = album, onFinished = onDismissRequest)

                /**
                 * Queue
                 */
                if (isPlaybackLoaded) {
                    PlayNextMediaOption(mediaImpl = album, onFinished = onDismissRequest)
                    AddToQueueDialogOption(mediaImpl = album, onFinished = onDismissRequest)
                }

                /**
                 * Redirections
                 */
                NavigateToMediaMusicOption(
                    mediaImpl = album.artist!!,
                    navController = navController
                )
            }
        }
    )
}

@Preview
@Composable
private fun AlbumOptionsDialogPreview() {
    val navController: NavHostController = rememberNavController()
    AlbumOptionsDialog(
        navController = navController,
        album = Album(title = "Album title"),
        onDismissRequest = {}
    )
}