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

package io.github.antoinepirlot.satunes.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.size
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager

/**
 * @author Antoine Pirlot on 20/08/2024
 */

class ClassicPlaybackWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                ClassicPlaybackWidgetComposable(context = context)
            }
        }
    }

    @Composable
    private fun ClassicPlaybackWidgetComposable(
        modifier: GlanceModifier = GlanceModifier,
        context: Context
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isLoaded: Boolean by PlaybackManager.isLoaded

            if (isLoaded) {
                PreviousButton(context = context)
                Spacer(modifier = GlanceModifier.size(16.dp))
            }

            PlayPauseButton(modifier = GlanceModifier.size(60.dp), context = context)

            if (isLoaded) {
                Spacer(modifier = GlanceModifier.size(16.dp))
                NextButton(context = context)
            }
        }
    }

    @Composable
    private fun PlayPauseButton(
        modifier: GlanceModifier = GlanceModifier,
        context: Context
    ) {
        val isPlaying: Boolean by PlaybackManager.isPlaying
        if (isPlaying) {
            SquareIconButton(
                modifier = modifier,
                imageProvider = SatunesIcons.PAUSE.imageProvider!!,
                contentDescription = "Pause",
                onClick = { PlaybackManager.pause(context = context) },
                backgroundColor = GlanceTheme.colors.background,
                contentColor = GlanceTheme.colors.onSurface,
            )
        } else {
            CircleIconButton(
                modifier = modifier,
                imageProvider = SatunesIcons.PLAY.imageProvider!!,
                contentDescription = "Play",
                onClick = { PlaybackManager.play(context = context) },
                backgroundColor = GlanceTheme.colors.background,
                contentColor = GlanceTheme.colors.onSurface,
            )
        }
    }

    @Composable
    private fun PreviousButton(
        modifier: GlanceModifier = GlanceModifier,
        context: Context,
    ) {
        CircleIconButton(
            modifier = modifier,
            imageProvider = SatunesIcons.SKIP_PREVIOUS.imageProvider!!,
            contentDescription = "Skip previous",
            onClick = { PlaybackManager.playPrevious(context = context) },
        )
    }

    @Composable
    private fun NextButton(
        modifier: GlanceModifier = GlanceModifier,
        context: Context,
    ) {
        CircleIconButton(
            modifier = modifier,
            imageProvider = SatunesIcons.SKIP_NEXT.imageProvider!!,
            contentDescription = "Skip next",
            onClick = { PlaybackManager.playNext(context = context) }
        )
    }
}