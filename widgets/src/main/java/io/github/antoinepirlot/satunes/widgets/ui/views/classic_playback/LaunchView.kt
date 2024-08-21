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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.glance.Button
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.CircularProgressIndicator
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import io.github.antoinepirlot.satunes.widgets.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 21/08/2024
 */

@Composable
@GlanceComposable
internal fun LaunchView(
    modifier: GlanceModifier = GlanceModifier,
) {
    val context: Context = LocalContext.current
    var isLoading: Boolean by rememberSaveable { mutableStateOf(false) }

    if (isLoading) {
        CircularProgressIndicator(modifier = modifier)
    } else {
        Button(
            modifier = modifier,
            text = context.getString(R.string.launch_text),
            onClick = {
                isLoading = true
                SatunesLogger.getLogger().info("Launching from widget")
                CoroutineScope(Dispatchers.IO).launch {
                    SettingsManager.loadSettings(context = context)
                    PlaybackManager.initPlaybackWithAllMusics(context = context)
                    isLoading = false
                }
            }
        )
    }
}