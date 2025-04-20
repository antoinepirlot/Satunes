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

import androidx.compose.runtime.Composable
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import io.github.antoinepirlot.satunes.database.models.Music

/**
 * @author Antoine Pirlot on 20/08/2024
 */

@Composable
@GlanceComposable
internal fun MusicInformations(
    modifier: GlanceModifier = GlanceModifier,
    music: Music
) {
    Text(
        modifier = modifier,
        text = music.title + " - " + music.artist.title,
        style = TextStyle(color = GlanceTheme.colors.onSurface),
        maxLines = 1
    )
}