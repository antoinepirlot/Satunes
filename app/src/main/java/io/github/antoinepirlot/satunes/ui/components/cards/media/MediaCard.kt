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

package io.github.antoinepirlot.satunes.ui.components.cards.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.components.texts.Subtitle
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.ui.components.cards.ListItem
import io.github.antoinepirlot.satunes.ui.components.images.MediaArtwork
import io.github.antoinepirlot.satunes.utils.getMediaTitle

/**
 * @author Antoine Pirlot on 16/01/24
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MediaCard(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel(),
    mediaImpl: MediaImpl,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?
) {
    val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()

    val title: String = getMediaTitle(mediaImpl = mediaImpl)
    val screenWidthDp: Int = LocalConfiguration.current.screenWidthDp
    val boxModifier: Modifier = if (onClick != null || onLongClick != null) {
        modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = onLongClick
        )
    } else modifier
    Box(modifier = boxModifier) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent), //Without Transparent, in that specific case it's white
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
                    if (navigationUiState.currentDestination == Destination.ALBUM && mediaImpl is Music && mediaImpl.cdTrackNumber != null)
                        NormalText(text = mediaImpl.cdTrackNumber.toString() + " - " + title)
                    else
                        NormalText(text = title)

                    //Use these as for the same thing the builder doesn't like in one
                    if (mediaImpl is Album)
                        Subtitle(text = mediaImpl.artist.title)
                    else if (mediaImpl is Music)
                        Subtitle(text = mediaImpl.album.title + " - " + mediaImpl.artist.title)
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
        addedDateMs = 0,
    )
    MediaCard(
        modifier = Modifier.fillMaxSize(),
        mediaImpl = music,
        onClick = null,
        onLongClick = null
    )
}