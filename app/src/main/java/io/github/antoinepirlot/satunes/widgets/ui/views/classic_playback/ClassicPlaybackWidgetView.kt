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

package io.github.antoinepirlot.satunes.widgets.ui.views.classic_playback

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.size
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager
import io.github.antoinepirlot.satunes.widgets.ui.components.Artwork
import io.github.antoinepirlot.satunes.widgets.ui.components.MusicInformations
import io.github.antoinepirlot.satunes.widgets.ui.components.PlaybackControlBar
import io.github.antoinepirlot.satunes.widgets.ui.components.buttons.OpenSatunesButton

/**
 * @author Antoine Pirlot on 21/08/2024
 */


@Composable
@GlanceComposable
internal fun ClassicPlaybackWidgetView(
    modifier: GlanceModifier = GlanceModifier,
) {
    val isDataLoading: Boolean by DataLoader.isLoading
    val isPlaybackLoading: Boolean by PlaybackManager.isLoading
    if (isDataLoading || isPlaybackLoading) {
        CircularProgressIndicator(modifier = modifier)
        return
    }

    val isDataLoaded: Boolean by DataLoader.isLoaded
    if (!isDataLoaded) {
        OpenSatunesButton(modifier = modifier)
    } else {
        WidgetView(modifier = modifier)
    }
}

@Composable
@GlanceComposable
private fun WidgetView(
    modifier: GlanceModifier = GlanceModifier,
) {
    val context: Context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        PlaybackManager.checkPlaybackController(context = context)
    }

    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val musicPlaying: Music? by PlaybackManager.musicPlaying

        if (musicPlaying != null) {
            Artwork(music = musicPlaying!!)
            Spacer(modifier = GlanceModifier.size(5.dp))
        }

        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (musicPlaying != null) {
                MusicInformations(music = musicPlaying!!)
                Spacer(modifier = GlanceModifier.size(5.dp))
            }
            PlaybackControlBar()
        }
    }
}