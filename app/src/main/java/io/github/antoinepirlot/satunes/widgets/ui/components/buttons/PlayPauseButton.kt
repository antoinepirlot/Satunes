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

package io.github.antoinepirlot.satunes.widgets.ui.components.buttons

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.SquareIconButton
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager

/**
 * @author Antoine Pirlot on 20/08/2024
 */

@Composable
@GlanceComposable
internal fun PlayPauseButton(
    modifier: GlanceModifier = GlanceModifier,
) {
    val context: Context = LocalContext.current
    val isPlaying: Boolean by PlaybackManager.isPlaying
    if (isPlaying) {
        SquareIconButton(
            modifier = modifier,
            imageProvider = SatunesIcons.PAUSE.imageProvider!!,
            contentDescription = "Pause",
            onClick = { PlaybackManager.pause(context = context) },
            backgroundColor = GlanceTheme.colors.primary,
            contentColor = GlanceTheme.colors.onPrimary,
        )
    } else {
        CircleIconButton(
            modifier = modifier,
            imageProvider = SatunesIcons.PLAY.imageProvider!!,
            contentDescription = "Play",
            onClick = { PlaybackManager.play(context = context) },
            backgroundColor = GlanceTheme.colors.primary,
            contentColor = GlanceTheme.colors.onPrimary,
        )
    }
}