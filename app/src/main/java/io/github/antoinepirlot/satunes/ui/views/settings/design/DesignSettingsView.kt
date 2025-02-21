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

package io.github.antoinepirlot.satunes.ui.views.settings.design

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.ui.components.settings.design.list.ListSubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.design.navigation_bar.NavigationBarSubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.design.playback.PlaybackDesignSubSettings

/**
 *   @author Antoine Pirlot 06/03/2024
 */

@Composable
internal fun DesignSettingsView(
    modifier: Modifier = Modifier
) {
    val scrollState: ScrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        Title(text = stringResource(id = R.string.design_setting_title))
        NavigationBarSubSettings()
        PlaybackDesignSubSettings()
        ListSubSettings()
    }
}

@Composable
@Preview
private fun BottomNavigationBarSettingsViewPreview() {
    DesignSettingsView()
}