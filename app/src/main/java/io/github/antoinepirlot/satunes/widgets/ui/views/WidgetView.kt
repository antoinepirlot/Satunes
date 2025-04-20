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

package io.github.antoinepirlot.satunes.widgets.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager
import io.github.antoinepirlot.satunes.widgets.ui.components.buttons.LoadSatunesButton

/**
 * @author Antoine Pirlot 20/04/2025
 */
@Composable
@GlanceComposable
fun WidgetView(
    modifier: GlanceModifier = GlanceModifier,
    widgetView: @Composable @GlanceComposable () -> Unit
) {
    val isDataLoading: Boolean by DataLoader.isLoading
    val isPlaybackLoading: Boolean by PlaybackManager.isLoading
    GlanceTheme {
        Scaffold(
            modifier = GlanceModifier.clickable(onClick = actionStartActivity<MainActivity>())
        ) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isDataLoading || isPlaybackLoading) {
                    CircularProgressIndicator(modifier = modifier)
                    return@Box
                }

                val isDataLoaded: Boolean by DataLoader.isLoaded
                if (!isDataLoaded) {
                    LoadSatunesButton(modifier = modifier)
                } else {
                    widgetView()
                }
            }
        }
    }
}