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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.cards.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.components.texts.Subtitle
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.models.DestinationCategory
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.cards.ListItem
import io.github.antoinepirlot.satunes.ui.components.dialog.media.MediaOptionsDialog
import io.github.antoinepirlot.satunes.ui.components.images.MediaArtwork
import io.github.antoinepirlot.satunes.ui.utils.getRootFolderName
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 16/01/24
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MediaCard(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    mediaImpl: MediaImpl,
    enableExtraOptions: Boolean = true,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    val navController: NavHostController = LocalNavController.current
    val haptics: HapticFeedback = LocalHapticFeedback.current
    val isInPlaybackView: Boolean =
        satunesUiState.currentDestination.category == DestinationCategory.PLAYBACK

    val showMediaOptions: Boolean = satunesUiState.mediaToShowOptions == mediaImpl
    val title: String =
        if (mediaImpl is Folder && mediaImpl.parentFolder == null) {
            getRootFolderName(title = mediaImpl.title)
        } else if (mediaImpl is Playlist && mediaImpl.title == LIKES_PLAYLIST_TITLE) {
            stringResource(id = RDb.string.likes_playlist_title)
        } else {
            mediaImpl.title
        }
    val screenWidthDp: Int = LocalConfiguration.current.screenWidthDp
    val boxModifier: Modifier = if (!satunesUiState.showMediaSelectionDialog) {
        modifier.combinedClickable(
            onClick = {
                if (!showMediaOptions) {
                    if (mediaImpl is Music && !isInPlaybackView)
                        playbackViewModel.loadMusicFromMedias(
                            medias = dataUiState.mediaImplListOnScreen,
                            musicToPlay = mediaImpl
                        )
                    openMedia(
                        playbackViewModel = playbackViewModel,
                        media = mediaImpl,
                        navController = if (isInPlaybackView) null else navController,
                    )
                }
            },
            onLongClick = if (enableExtraOptions) {
                {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    satunesViewModel.showMediaOptionsOf(mediaImpl = mediaImpl)
                }
            } else null
        )
    } else modifier
    Box(modifier = boxModifier) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Unspecified), //Without unspecified, in that specific case it's white
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
                    if (mediaImpl == playbackViewModel.musicPlaying) {
                        val playingIcon: SatunesIcons = SatunesIcons.MUSIC_PLAYING
                        Icon(
                            modifier = imageModifier,
                            imageVector = playingIcon.imageVector,
                            contentDescription = playingIcon.description
                        )
                    } else {
                        MediaArtwork(mediaImpl = mediaImpl)
                    }
                }
            },
            headlineContent = {
                Column {
                    if (satunesUiState.currentDestination == Destination.ALBUM && mediaImpl is Music && mediaImpl.cdTrackNumber != null) {
                        NormalText(text = mediaImpl.cdTrackNumber.toString() + " - " + title)
                    } else {
                        NormalText(text = title)
                    }
                    //Use these as for the same thing the builder doesn't like in one
                    if (mediaImpl is Album) {
                        Subtitle(text = mediaImpl.artist.title)
                    } else if (mediaImpl is Music) {
                        Subtitle(text = mediaImpl.album.title + " - " + mediaImpl.artist.title)
                    }
                }
            },
            trailingContent = {
                if (mediaImpl is Music) {
                    val liked: Boolean by mediaImpl.liked
                    if (liked) {
                        val likedIcon: SatunesIcons = SatunesIcons.LIKED
                        Icon(
                            imageVector = likedIcon.imageVector,
                            contentDescription = likedIcon.description
                        )
                    }
                }
            }
        )
    }
    HorizontalDivider(modifier = modifier)

    // Media option dialog
    if (showMediaOptions) {
        MediaOptionsDialog(
            mediaImpl = mediaImpl,
            onDismissRequest = {
                satunesViewModel.hideMediaOptions()
            }
        )
    }
}

@Composable
@Preview
private fun CardPreview() {
    val artist = Artist(title = "Artist Title")
    val music = Music(
        id = 1,
        title = "",
        displayName = "Il avait les mots",
        absolutePath = "absolute path",
        duration = 2,
        size = 2,
        folder = Folder(title = "Folder"),
        artist = artist,
        album = Album(title = "Album Title", artist = artist),
        genre = Genre(title = "Genre Title"),
    )
    MediaCard(
        modifier = Modifier.fillMaxSize(),
        mediaImpl = music,
    )
}