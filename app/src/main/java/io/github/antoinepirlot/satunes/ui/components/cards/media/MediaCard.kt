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

package io.github.antoinepirlot.satunes.ui.components.cards.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.R
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.ui.ScreenSizes
import io.github.antoinepirlot.satunes.ui.components.cards.ListItem
import io.github.antoinepirlot.satunes.ui.components.dialog.album.AlbumOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.artist.ArtistOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.folder.FolderOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.genre.GenreOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.music.MusicOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.dialog.playlist.PlaylistOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.components.texts.Subtitle
import io.github.antoinepirlot.satunes.ui.utils.getRootFolderName
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 16/01/24
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MediaCard(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    media: MediaImpl,
    onClick: () -> Unit,
    openedPlaylist: Playlist?,
) {
    val haptics = LocalHapticFeedback.current
    var showMusicOptions: Boolean by rememberSaveable { mutableStateOf(false) }
    var showPlaylistOptions: Boolean by rememberSaveable { mutableStateOf(false) }
    var showArtistOptions: Boolean by rememberSaveable { mutableStateOf(false) }
    var showAlbumOptions: Boolean by rememberSaveable { mutableStateOf(false) }
    var showGenreOptions: Boolean by rememberSaveable { mutableStateOf(false) }
    var showFolderOptions: Boolean by rememberSaveable { mutableStateOf(false) }

    val title: String =
        if (media is Folder && media.parentFolder == null) {
            getRootFolderName(title = media.title)
        } else if (media is Playlist && media.title == LIKES_PLAYLIST_TITLE) {
            stringResource(id = RDb.string.likes_playlist_title)
        } else {
            media.title
        }
    val screenWidthDp: Int = LocalConfiguration.current.screenWidthDp
    Box(
        modifier = modifier.combinedClickable(
            onClick = {
                if (!showMusicOptions) {
                    onClick()
                }
                showMusicOptions = false
            },
            onLongClick = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                when (media) {
                    is Music -> showMusicOptions = true
                    is Playlist -> showPlaylistOptions = true
                    is Artist -> showArtistOptions = true
                    is Album -> showAlbumOptions = true
                    is Genre -> showGenreOptions = true
                    is Folder -> showFolderOptions = true
                }
            }
        ),
    ) {
        ListItem(
            headlineContent = {
                Column {
                    NormalText(text = title)
                    //Use these as for the same thing the builder doesn't like in one
                    if (media is Album) {
                        Subtitle(text = media.artist!!.title)
                    } else if (media is Music) {
                        Subtitle(text = media.artist.title)
                    }
                }
            },
            leadingContent = {
                val boxSize: Dp = if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL)
                    25.dp
                else
                    55.dp
                Box(
                    modifier = modifier
                        .fillMaxHeight()
                        .width(boxSize)
                ) {
                    val imageModifier: Modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                    val playbackController: PlaybackController = PlaybackController.getInstance()
                    if (media == playbackController.musicPlaying.value) {
                        val playingIcon: SatunesIcons = SatunesIcons.MUSIC_PLAYING
                        Icon(
                            modifier = imageModifier,
                            imageVector = playingIcon.imageVector,
                            contentDescription = playingIcon.description
                        )
                    } else {
                        if (media.artwork != null) {
                            Image(
                                modifier = imageModifier,
                                bitmap = media.artwork!!.asImageBitmap(),
                                contentDescription = "Media Artwork"
                            )
                        } else {
                            if (media is Music || media is Album) {
                                //Use it will prevent slow devices showing icon instead of artwork
                                val emptyArtwork: ImageBitmap = ResourcesCompat.getDrawable(
                                    LocalContext.current.resources,
                                    R.mipmap.empty_album_artwork_foreground,
                                    null
                                )?.toBitmap()!!.asImageBitmap()
                                Image(bitmap = emptyArtwork, contentDescription = "Empty Album")
                            } else {
                                val mediaIcon: SatunesIcons =
                                    getRightIconAndDescription(media = media)
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
                }
            }
        )
    }
    HorizontalDivider(modifier = modifier)

    // Music options dialog
    if (showMusicOptions && media is Music) {
        MusicOptionsDialog(
            navController = navController,
            music = media,
            playlist = openedPlaylist,
            onDismissRequest = { showMusicOptions = false },
        )
    }

    // PlaylistDB option dialog
    if (showPlaylistOptions && media is Playlist) {
        PlaylistOptionsDialog(
            playlist = media,
            onDismissRequest = { showPlaylistOptions = false }
        )
    }

    // Artist option dialog
    if (showArtistOptions && media is Artist) {
        ArtistOptionsDialog(
            artist = media,
            onDismissRequest = { showArtistOptions = false }
        )
    }

    // Album option dialog
    if (showAlbumOptions && media is Album) {
        AlbumOptionsDialog(
            navController = navController,
            album = media,
            onDismissRequest = { showAlbumOptions = false }
        )
    }

    // Genre option dialog
    if (showGenreOptions && media is Genre) {
        GenreOptionsDialog(
            genre = media,
            onDismissRequest = { showGenreOptions = false }
        )
    }

    // Folder option dialog
    if (showFolderOptions && media is Folder) {
        FolderOptionsDialog(
            folder = media,
            onDismissRequest = { showFolderOptions = false }
        )
    }
}

private fun getRightIconAndDescription(media: MediaImpl): SatunesIcons {
    return when (media) {
        is Folder -> SatunesIcons.FOLDER
        is Artist -> SatunesIcons.ARTIST
        is Album -> SatunesIcons.ALBUM
        is Genre -> SatunesIcons.GENRES
        is Playlist -> SatunesIcons.PLAYLIST
        else -> SatunesIcons.MUSIC // In that case, mediaImpl is Music
    }
}

@Composable
@Preview
private fun CardPreview() {
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
    val navController: NavHostController = rememberNavController()
    MediaCard(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        media = music,
        onClick = {},
        openedPlaylist = null
    )
}