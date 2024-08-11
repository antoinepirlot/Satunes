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

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.icons.R
import io.github.antoinepirlot.satunes.ui.utils.getRightIconAndDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 29/02/24
 */

@Composable
internal fun MediaArtwork(
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
        val context: Context = LocalContext.current

        var artwork: ImageBitmap? by remember { mutableStateOf(null) }
        var job: Job? = null
        LaunchedEffect(key1 = Unit) {
            job?.cancel()
            job = CoroutineScope(Dispatchers.IO).launch {
                artwork = when (mediaImpl) {
                    is Music -> mediaImpl.getAlbumArtwork(context = context)
                    is Album -> mediaImpl.getMusicSet().first()
                        .getAlbumArtwork(context = context)

                    else -> null
                }?.asImageBitmap()
            }
        }

        if (artwork != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = artwork!!,
                contentDescription = "Music Album Artwork"
            )
        } else {
            if (mediaImpl is Music || mediaImpl is Album) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.mipmap.empty_album_artwork_foreground),
                    contentDescription = "Default Album Artwork"
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center),
                    icon = getRightIconAndDescription(media = mediaImpl)
                )
            }
        }
    }
}

@Composable
@Preview
private fun AlbumArtworkPreview() {
    MediaArtwork(mediaImpl = Album(title = "", artist = Artist(title = "Artist Title")))
}