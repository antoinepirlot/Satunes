/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.

 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.ui.components.cards.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.R
import earth.mp3player.database.models.Album
import earth.mp3player.database.models.Artist
import earth.mp3player.database.models.Folder
import earth.mp3player.database.models.Genre
import earth.mp3player.database.models.Media
import earth.mp3player.database.models.Music
import earth.mp3player.database.models.relations.PlaylistWithMusics
import earth.mp3player.database.models.tables.MusicDB
import earth.mp3player.database.services.DatabaseManager
import earth.mp3player.services.PlaylistSelectionManager
import earth.mp3player.ui.components.music.options.MusicOptionsDialog
import earth.mp3player.ui.components.playlist.PlaylistOptionsDialog
import earth.mp3player.ui.views.MP3PlayerIcons

/**
 * @author Antoine Pirlot on 16/01/24
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    media: Media,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    var showMusicOptions: Boolean by rememberSaveable { mutableStateOf(false) }
    var showPlaylistOptions: Boolean by rememberSaveable { mutableStateOf(false) }
    val title: String =
        if (media is Folder && media.parentFolder == null) {
            when (media.title) {
                "0" -> stringResource(id = R.string.this_device)

                else -> "${stringResource(id = R.string.external_storage)}: ${media.title}"
            }
        } else if (media is PlaylistWithMusics) {
            media.playlist.title
        } else if (media is MusicDB) {
            media.music.title
        } else {
            media.title
        }

    Box(
        modifier = modifier.combinedClickable(
            onClick = {
                if (!showMusicOptions) {
                    onClick()
                }
                showMusicOptions = false
            },
            onLongClick = {
                when (media) {
                    is Music -> {
                        if (!showMusicOptions) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        showMusicOptions = !showMusicOptions
                    }

                    is PlaylistWithMusics -> {
                        if (!showMusicOptions) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        showPlaylistOptions = !showPlaylistOptions
                    }
                }
            }
        ),
    ) {
        ListItem(
            headlineContent = {
                Text(text = title)
            },
            leadingContent = {
                val mediaIcon: MP3PlayerIcons = getRightIconAndDescription(media = media)
                Icon(
                    imageVector = mediaIcon.imageVector,
                    contentDescription = mediaIcon.description
                )
            }
        )
    }
    Divider(modifier = modifier)

    // Music options dialog
    if (showMusicOptions && media is Music) {
        val context = LocalContext.current
        MusicOptionsDialog(
            musicTitle = title,
            onAddToPlaylist = {
                val db = DatabaseManager(context = context)
                db.insertMusicToPlaylists(
                    music = media,
                    playlists = PlaylistSelectionManager.checkedPlaylistWithMusics
                )
                showMusicOptions = false
            },
            onRemoveFromPlaylist = {
                val db = DatabaseManager(context = context)
                db.removeMusicFromPlaylist(
                    music = media,
                    playlist = PlaylistSelectionManager.openedPlaylist!!
                )
                showMusicOptions = false
            },
            onDismissRequest = { showMusicOptions = false }
        )
    }

    // Playlist option dialog
    if (showPlaylistOptions && media is PlaylistWithMusics) {
        val context = LocalContext.current
        PlaylistOptionsDialog(
            playlistTitle = media.title,
            onRemovePlaylist = {
                val db = DatabaseManager(context = context)
                db.removePlaylist(playlistToRemove = media)
                showPlaylistOptions = false
            },
            onDismissRequest = { showPlaylistOptions = false }
        )
    }
}

private fun getRightIconAndDescription(media: Media): MP3PlayerIcons {
    return when (media) {
        is Folder -> MP3PlayerIcons.FOLDER
        is Artist -> MP3PlayerIcons.ARTIST
        is Album -> MP3PlayerIcons.ALBUM
        is Genre -> MP3PlayerIcons.GENRES
        is PlaylistWithMusics -> MP3PlayerIcons.PLAYLIST
        else -> MP3PlayerIcons.MUSIC // In that case, media is Music
    }
}

@Composable
@Preview
fun CardPreview() {
    val music = Music(
        id = 1,
        title = "",
        displayName = "Il avait les mots",
        duration = 2,
        size = 2,
        relativePath = "relative path",
        context = LocalContext.current
    )
    MediaCard(
        modifier = Modifier.fillMaxSize(),
        media = music,
        onClick = {})
}