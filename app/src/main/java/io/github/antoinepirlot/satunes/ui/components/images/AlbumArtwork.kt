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

package io.github.antoinepirlot.satunes.ui.components.images

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.icons.R
import io.github.antoinepirlot.satunes.ui.ScreenSizes
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel

/**
 * @author Antoine Pirlot on 29/02/24
 */

@Composable
internal fun AlbumArtwork(
    modifier: Modifier = Modifier,
    mediaImpl: MediaImpl,
    onClick: ((album: Album?) -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val clickableModifier: Modifier = if (onClick != null) {
        modifier
            .size(
                if (screenWidthDp >= (ScreenSizes.VERY_VERY_SMALL - 1) && screenWidthDp < ScreenSizes.VERY_SMALL) 150.dp
                else if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) 100.dp
                else 300.dp // Normal
            )
            .clickable {
                onClick(
                    when (mediaImpl) {
                        is Music -> mediaImpl.album
                        is Album -> mediaImpl
                        else -> null
                    }
                )
            }
    } else {
        modifier
            .size(
                if (screenWidthDp >= (ScreenSizes.VERY_VERY_SMALL - 1) && screenWidthDp < ScreenSizes.VERY_SMALL) 150.dp
                else if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) 100.dp
                else 300.dp // Normal
            )
    }

    Box(
        modifier = clickableModifier,
        contentAlignment = contentAlignment
    ) {
        if (mediaImpl.artwork != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = mediaImpl.artwork!!.asImageBitmap(),
                contentDescription = "Music Playing Album Artwork"
            )
        } else {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.mipmap.empty_album_artwork_foreground),
                contentDescription = "Default Album Artwork"
            )
        }
    }
}

@Composable
internal fun MusicPlayingAlbumArtwork(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    onClick: (album: Album?) -> Unit = { /* Do nothing by default */ }
) {
    AlbumArtwork(
        modifier = modifier,
        mediaImpl = playbackViewModel.musicPlaying!!,
        onClick = onClick
    )
}

@Composable
@Preview
private fun AlbumArtworkPreview() {
    AlbumArtwork(mediaImpl = Album(title = "", artist = Artist(title = "Artist Title")))
}