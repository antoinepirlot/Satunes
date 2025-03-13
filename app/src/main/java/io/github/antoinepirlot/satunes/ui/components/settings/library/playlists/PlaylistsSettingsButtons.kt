/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.settings.library.playlists

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.ui.components.buttons.settings.library.playlists.CleanPlaylistsButton
import io.github.antoinepirlot.satunes.ui.components.buttons.settings.library.playlists.ExportPlaylistsButton
import io.github.antoinepirlot.satunes.ui.components.buttons.settings.library.playlists.ImportPlaylistsButton

/**
 * @author Antoine Pirlot on 12/09/2024
 */

@Composable
internal fun PlaylistsSettingsButtons(modifier: Modifier = Modifier) {
    val scrollState: ScrollState = rememberScrollState()
    Row(
        modifier = modifier.horizontalScroll(state = scrollState)
    ) {
        Spacer(modifier = Modifier.size(8.dp))
        ExportPlaylistsButton()
        Spacer(modifier = Modifier.size(8.dp))
        ImportPlaylistsButton()
        Spacer(modifier = Modifier.size(8.dp))
        CleanPlaylistsButton()
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Preview
@Composable
private fun PlaylistsSettingsButtonsPreview() {
    PlaylistsSettingsButtons()
}