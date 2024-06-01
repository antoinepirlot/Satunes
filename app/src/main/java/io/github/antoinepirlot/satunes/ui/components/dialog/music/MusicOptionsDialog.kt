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
import androidx.compose.ui.res.stringResource
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.DialogOption
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText

/**
 * @author Antoine Pirlot on 30/03/2024
 */


@Composable
fun MusicOptionsDialog(
    modifier: Modifier = Modifier,
    music: Music,
    openPlaylistWithMusics: PlaylistWithMusics? = null,
    onRemoveFromPlaylist: () -> Unit,
    onDismissRequest: () -> Unit
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
                AddToPlaylistOption(music = music, onFinished = onDismissRequest)

                if (openPlaylistWithMusics != null) {
                    DialogOption(
                        onClick = {
                            onRemoveFromPlaylist()
                            onDismissRequest()
                        },
                        icon = {
                            val playlistRemoveIcon: SatunesIcons = SatunesIcons.PLAYLIST_REMOVE
                            Icon(
                                imageVector = playlistRemoveIcon.imageVector,
                                contentDescription = playlistRemoveIcon.description
                            )
                        },
                        text = stringResource(id = R.string.remove_from_playlist)
                    )
                }
            }
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = { /* Nothing */ }
    )
}