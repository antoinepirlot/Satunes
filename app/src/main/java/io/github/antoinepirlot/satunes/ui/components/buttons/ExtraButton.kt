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

package io.github.antoinepirlot.satunes.ui.components.buttons

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.images.Icon

/**
 * @author Antoine Pirlot on 20/04/2024
 */

@Composable
internal fun ExtraButton(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    icon: SatunesIcons,
    description: String? = null,
    onClick: () -> Unit,
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    if (icon == SatunesIcons.SHUFFLE && satunesUiState.shuffleMode)
        return //The shuffle mode is always activated by default and don't need to be shown
    val screenWidthDp: Int = LocalConfiguration.current.screenWidthDp
    val buttonSize: Dp = if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL)
        50.dp
    else
        60.dp
    FloatingActionButton(
        modifier = modifier
            .padding(bottom = 8.dp)
            .size(buttonSize),
        onClick = onClick
    ) {
        Icon(
            icon = icon,
            modifier = Modifier.size(buttonSize / 2),
        )
    }
}

@Preview
@Composable
private fun ExtraButtonPreview() {
    ExtraButton(
        icon = SatunesIcons.PLAYLIST_ADD,
        onClick = {}
    )
}