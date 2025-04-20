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

package io.github.antoinepirlot.satunes.ui.components.settings.design.playback.artwork

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.models.SwitchSettings
import io.github.antoinepirlot.satunes.ui.components.settings.SettingsSwitchList
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings


/**
 * @author Antoine Pirlot 04/03/2025
 */
@Composable
internal fun ArtworkAnimationSubSetting(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel()
) {
    val artworkAnimationSwitchSettings: Map<SwitchSettings, Boolean> = mapOf(
        Pair(SwitchSettings.ARTWORK_ANIMATION, satunesViewModel.artworkAnimation),
        Pair(SwitchSettings.ARTWORK_CIRCLE_SHAPE, satunesViewModel.artworkCircleShape)
    )

    SubSettings(
        modifier = modifier,
        title = stringResource(R.string.artwork_sub_settings_title)
    ) {
        SettingsSwitchList(checkedMap = artworkAnimationSwitchSettings)
    }
}

@Preview
@Composable
private fun ArtworkAnimationSubSettingPreview(modifier: Modifier = Modifier) {
    ArtworkAnimationSubSetting()
}