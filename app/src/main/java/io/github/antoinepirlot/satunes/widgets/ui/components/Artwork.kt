/*
 * This file is part of Satunes.
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.widgets.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.utils.utils.toCircularBitmap
import io.github.antoinepirlot.satunes.icons.R as RIcon

/**
 * @author Antoine Pirlot on 20/08/2024
 */

@SuppressLint("UseCompatLoadingForDrawables")
@Composable
@GlanceComposable
internal fun Artwork(
    modifier: GlanceModifier = GlanceModifier,
    music: Music,
) {
    val context: Context = LocalContext.current
    val artworkCircleShape: Boolean by remember { SettingsManager.artworkCircleShape }

    var artwork: Bitmap? = music.getAlbumArtwork(context = context)
    if (artwork == null)
        artwork = context.getDrawable(RIcon.mipmap.empty_album_artwork_foreground)!!.toBitmap()
    if (artworkCircleShape) artwork = artwork.toCircularBitmap()

    Image(
        modifier = modifier,
        provider = ImageProvider(bitmap = artwork),
        contentDescription = "Artwork",
    )
}