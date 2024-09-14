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

package io.github.antoinepirlot.satunes.ui.components.dialog.genre

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToPlaylistMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.AddToQueueDialogOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.PlayNextMediaOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.RemoveFromQueueOption
import io.github.antoinepirlot.satunes.ui.components.dialog.media.options.ShareMediaOption

/**
 * @author Antoine Pirlot on 07/07/2024
 */

@Composable
internal fun GenreOptionsDialog(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    genre: Genre,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = { /* Nothing */ },
        icon = {
            val icon: SatunesIcons = SatunesIcons.GENRES
            Icon(imageVector = icon.imageVector, contentDescription = icon.description)
        },
        title = {
            NormalText(text = genre.title)
        },
        text = {
            Column {
                /**
                 * Playlist
                 */
                AddToPlaylistMediaOption(mediaImpl = genre, onFinished = onDismissRequest)

                /**
                 * Playback
                 */
                if (playbackViewModel.isLoaded && genre.musicCount() <= 500) {
                    PlayNextMediaOption(mediaImpl = genre, onDismissRequest = onDismissRequest)
                    AddToQueueDialogOption(mediaImpl = genre, onDismissRequest = onDismissRequest)
                    RemoveFromQueueOption(
                        mediaImpl = genre,
                        onDismissRequest = onDismissRequest
                    )
                }

                /**
                 * Share
                 */
                ShareMediaOption(media = genre)
            }
        }
    )
}

@Preview
@Composable
private fun GenreOptionsDialogPreview() {
    GenreOptionsDialog(genre = Genre(title = "Genre title"), onDismissRequest = {})
}