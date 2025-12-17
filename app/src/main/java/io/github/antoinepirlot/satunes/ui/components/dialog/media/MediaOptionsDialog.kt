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
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.Media
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist
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
    media: Media,
    onDismissRequest: () -> Unit
) {
    if (media.isMusic())
        MusicOptionsDialog(
            modifier = modifier,
            music = media as Music,
            onDismissRequest = onDismissRequest
        )
    else if (media.isArtist())
        ArtistOptionsDialog(
            modifier = modifier,
            artist = media as Artist,
            onDismissRequest = onDismissRequest
        )
    else if (media.isAlbum())
        AlbumOptionsDialog(
            modifier = modifier,
            album = media as Album,
            onDismissRequest = onDismissRequest
        )
    else if (media.isGenre())
        GenreOptionsDialog(
            modifier = modifier,
            genre = media as Genre,
            onDismissRequest = onDismissRequest
        )
    else if (media.isPlaylist())
        PlaylistOptionsDialog(
            modifier = modifier,
            playlist = media as Playlist,
            onDismissRequest = onDismissRequest
        )
    else if (media.isFolder())
        FolderOptionsDialog(
            modifier = modifier,
            folder = media as Folder,
            onDismissRequest = onDismissRequest
        )
}