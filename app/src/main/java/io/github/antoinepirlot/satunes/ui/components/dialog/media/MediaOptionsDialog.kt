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
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.dialog.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.ui.components.dialog.album.AlbumOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.artist.ArtistOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.folder.FolderOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.genre.GenreOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.music.MusicOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.playlist.PlaylistOptionsDialog

/**
 * @author Antoine Pirlot on 20/10/2024
 */

@Composable
internal fun MediaOptionsDialog(
    modifier: Modifier = Modifier,
    mediaImpl: MediaImpl,
    onDismissRequest: () -> Unit
) {
    when (mediaImpl) {
        is Music -> MusicOptionsDialog(
            modifier = modifier,
            music = mediaImpl,
            onDismissRequest = onDismissRequest
        )

        is Artist -> ArtistOptionsDialog(
            modifier = modifier,
            artist = mediaImpl,
            onDismissRequest = onDismissRequest
        )

        is Album -> AlbumOptionsDialog(
            modifier = modifier,
            album = mediaImpl,
            onDismissRequest = onDismissRequest
        )

        is Genre -> GenreOptionsDialog(
            modifier = modifier,
            genre = mediaImpl,
            onDismissRequest = onDismissRequest
        )

        is Playlist -> PlaylistOptionsDialog(
            modifier = modifier,
            playlist = mediaImpl,
            onDismissRequest = onDismissRequest
        )

        is Folder -> FolderOptionsDialog(
            modifier = modifier,
            folder = mediaImpl,
            onDismissRequest = onDismissRequest
        )
    }
}