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

package io.github.antoinepirlot.satunes.ui.components.bars.bottom

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.ui.components.bars.MediaNavBarSelection

/**
 * @author Antoine Pirlot on 03/02/24
 */

@Composable
internal fun BottomAppBar(
    modifier: Modifier = Modifier
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val navigationModifier: Modifier =
        if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) modifier.fillMaxHeight(0.11f) else modifier
    val navigationItemModifier: Modifier =
        if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) Modifier.size(16.dp) else Modifier

    val navBarSections: List<NavBarSection> = listOf(
        NavBarSection.FOLDERS,
        NavBarSection.ARTISTS,
        NavBarSection.ALBUMS,
        NavBarSection.GENRES,
        NavBarSection.MUSICS,
        NavBarSection.PLAYLISTS,
    )

    NavigationBar(modifier = navigationModifier) {
        for (navBarSection: NavBarSection in navBarSections) {
            val isEnabled: Boolean by navBarSection.isEnabled
            if (isEnabled) {
                MediaNavBarSelection(
                    modifier = navigationItemModifier,
                    navBarSection = navBarSection
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun SatunesBottomAppBarPreview() {
    BottomAppBar()
}