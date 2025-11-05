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

package io.github.antoinepirlot.satunes.ui.components.buttons.bars.bottom

import androidx.compose.foundation.layout.size
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.ui.components.images.Icon

/**
 * @author Antoine Pirlot on 3/02/24
 */

@Composable
internal fun ShowCurrentMusicButton(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
) {
    val navController: NavHostController = LocalNavController.current
    val haptics: HapticFeedback = LocalHapticFeedback.current

    val screenWidthDp: Int = LocalConfiguration.current.screenWidthDp
    val buttonSize: Dp = if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL)
        80.dp
    else
        100.dp
    LargeFloatingActionButton(
        modifier = modifier.size(buttonSize),
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            openCurrentMusic(
                playbackViewModel = playbackViewModel,
                navController = navController
            )
        }
    ) {
        Icon(
            modifier = Modifier.size(buttonSize / 1.5f),
            jetpackLibsIcons = JetpackLibsIcons.MUSIC
        )
    }
}

@Composable
@Preview
private fun ShowCurrentMusicPreview() {
    ShowCurrentMusicButton()
}