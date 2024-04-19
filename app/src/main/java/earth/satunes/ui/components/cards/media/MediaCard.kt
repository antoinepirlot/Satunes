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

package earth.satunes.ui.components.cards.media

import android.net.Uri.decode
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import earth.satunes.database.models.Album
import earth.satunes.database.models.Artist
import earth.satunes.database.models.Folder
import earth.satunes.database.models.Genre
import earth.satunes.database.models.Media
import earth.satunes.database.models.Music
import earth.satunes.database.models.relations.PlaylistWithMusics
import earth.satunes.database.models.tables.MusicDB
import earth.satunes.database.services.DatabaseManager
import earth.satunes.services.MediaSelectionManager
import earth.satunes.ui.components.music.options.MusicOptionsDialog
import earth.satunes.ui.components.playlist.PlaylistOptionsDialog
import earth.satunes.ui.views.SatunesIcons
import earth.satunes.ui.views.main.utils.getRootFolderName

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
            getRootFolderName(title = media.title)
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
                if (media is Album) {
                    Text(text = decode(title) + " - " + decode(media.artist!!.title))
                }
                Text(text = decode(title))
            },
            leadingContent = {
                val mediaIcon: SatunesIcons = getRightIconAndDescription(media = media)
                Icon(
                    imageVector = mediaIcon.imageVector,
                    contentDescription = mediaIcon.description
                )
            }
        )
    }
    HorizontalDivider(modifier = modifier)

    // Music options dialog
    if (showMusicOptions && media is Music) {
        val context = LocalContext.current
        MusicOptionsDialog(
            musicTitle = title,
            onAddToPlaylist = {
                val db = DatabaseManager(context = context)
                db.insertMusicToPlaylists(
                    music = media,
                    playlists = MediaSelectionManager.checkedPlaylistWithMusics
                )
                showMusicOptions = false
            },
            onRemoveFromPlaylist = {
                val db = DatabaseManager(context = context)
                db.removeMusicFromPlaylist(
                    music = media,
                    playlist = MediaSelectionManager.openedPlaylist!!
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
            playlistWithMusics = media,
            onRemovePlaylist = {
                val db = DatabaseManager(context = context)
                db.removePlaylist(playlistToRemove = media)
                showPlaylistOptions = false
            },
            onDismissRequest = { showPlaylistOptions = false }
        )
    }
}

private fun getRightIconAndDescription(media: Media): SatunesIcons {
    return when (media) {
        is Folder -> SatunesIcons.FOLDER
        is Artist -> SatunesIcons.ARTIST
        is Album -> SatunesIcons.ALBUM
        is Genre -> SatunesIcons.GENRES
        is PlaylistWithMusics -> SatunesIcons.PLAYLIST
        else -> SatunesIcons.MUSIC // In that case, media is Music
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
        folder = Folder(title = "Folder"),
        absolutePath = "absolute path",
        context = LocalContext.current
    )
    MediaCard(
        modifier = Modifier.fillMaxSize(),
        media = music,
        onClick = {}
    )
}