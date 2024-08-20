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

package io.github.antoinepirlot.satunes.ui.components.dialog.artist

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToPlaylistMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToQueueDialogOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.PlayNextMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.RemoveFromQueueOption

/**
 * @author Antoine Pirlot on 07/07/2024
 */

@Composable
internal fun ArtistOptionsDialog(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    artist: Artist,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = { /* Nothing */ },
        icon = {
            val icon: SatunesIcons = SatunesIcons.ARTIST
            Icon(imageVector = icon.imageVector, contentDescription = icon.description)
        },
        title = {
            NormalText(text = artist.title)
        },
        text = {
            Column {
                /**
                 * Playlist
                 */
                AddToPlaylistMediaOption(mediaImpl = artist, onFinished = onDismissRequest)

                /**
                 * Playback
                 */
                if (playbackViewModel.isLoaded) {
                    PlayNextMediaOption(mediaImpl = artist, onDismissRequest = onDismissRequest)
                    AddToQueueDialogOption(mediaImpl = artist, onDismissRequest = onDismissRequest)
                    RemoveFromQueueOption(mediaImpl = artist, onDismissRequest = onDismissRequest)
                }
            }
        }
    )
}

@Preview
@Composable
private fun ArtistOptionsDialogPreview() {
    ArtistOptionsDialog(artist = Artist(title = "Artist title"), onDismissRequest = {})
}