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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.settings.reset.navigation_bar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings
import io.github.antoinepirlot.satunes.ui.components.settings.reset.design.ResetListsSettings
import io.github.antoinepirlot.satunes.ui.components.settings.reset.design.ResetNavigationBarSettings

/**
 * @author Antoine Pirlot on 23/11/2024
 */
@Composable
internal fun ResetInterfaceSubSettings(
    modifier: Modifier = Modifier,
) {
    SubSettings(
        modifier = modifier,
        title = stringResource(id = R.string.design_setting_title)
    ) {
        ResetNavigationBarSettings()
        ResetListsSettings()
    }
}

@Preview
@Composable
private fun ResetInterfaceSubSettingsPreview() {
    ResetInterfaceSubSettings()
}