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
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import earth.satunes.ui.components.AlbumArtwork
import earth.satunes.ui.components.dialog.MusicOptionsDialog
import earth.satunes.ui.components.dialog.PlaylistOptionsDialog
import earth.satunes.ui.components.texts.NormalText
import earth.satunes.ui.components.texts.Subtitle
import earth.satunes.ui.views.SatunesIcons
import earth.satunes.ui.views.utils.getRootFolderName

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
            modifier = Modifier.height(70.dp),
            headlineContent = {
                Column {
                    NormalText(text = title)
                    //Use two if for same thing because compilator doesn't like in one if for media.artist
                    if (media is Album) {
                        Subtitle(text = media.artist!!.title)
                    } else if (media is Music) {
                        Subtitle(text = media.artist!!.title)
                    }
                }
            },
            leadingContent = {
                Box(modifier = modifier
                    .fillMaxHeight()
                    .width(55.dp)) {
                    if (media.artwork != null) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center),
                            bitmap = media.artwork!!,
                            contentDescription = "Media Artwork"
                        )
                    } else {
                        val mediaIcon: SatunesIcons = getRightIconAndDescription(media = media)
                        Icon(
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.Center),
                            imageVector = mediaIcon.imageVector,
                            contentDescription = mediaIcon.description
                        )
                    }
                }
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
        album = Album(title = "Album Title"),
        artist = Artist(title = "Artist Title"),
        genre = Genre(title = "Genre Title"),
        absolutePath = "absolute path",
        context = LocalContext.current
    )
    MediaCard(
        modifier = Modifier.fillMaxSize(),
        media = music,
        onClick = {}
    )
}