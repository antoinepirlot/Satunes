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

package io.github.antoinepirlot.satunes.widgets.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.size
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager

/**
 * @author Antoine Pirlot on 20/08/2024
 */

@Composable
@GlanceComposable
internal fun PlaybackControlBar(
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val musicPlaying: Music? by PlaybackManager.musicPlaying
        val spacerSize: Dp = 12.dp

        if (musicPlaying != null) {
            PreviousButton(modifier = GlanceModifier.size(40.dp))
            Spacer(modifier = GlanceModifier.size(spacerSize))
        }

        val playPauseSize: Dp = if (musicPlaying != null) 40.dp else 60.dp
        PlayPauseButton(modifier = GlanceModifier.size(playPauseSize))

        if (musicPlaying != null) {
            Spacer(modifier = GlanceModifier.size(spacerSize))
            NextButton(modifier = GlanceModifier.size(40.dp))
        }
    }
}