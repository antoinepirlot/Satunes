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

package io.github.antoinepirlot.satunes.ui.components.bars

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.ui.ScreenSizes
import io.github.antoinepirlot.satunes.ui.viewmodels.SatunesViewModel

/**
 * @author Antoine Pirlot on 03/02/24
 */

@Composable
internal fun SatunesBottomAppBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val navigationModifier: Modifier =
        if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) modifier.fillMaxHeight(0.11f) else modifier
    val navigationItemModifier: Modifier =
        if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) Modifier.size(16.dp) else Modifier

    val navBarSections: Map<NavBarSection, Boolean> = mapOf(
        Pair(NavBarSection.FOLDERS, satunesViewModel.foldersChecked),
        Pair(NavBarSection.ARTISTS, satunesViewModel.artistsChecked),
        Pair(NavBarSection.ALBUMS, satunesViewModel.albumsChecked),
        Pair(NavBarSection.GENRES, satunesViewModel.genresChecked),
        Pair(NavBarSection.MUSICS, true), // Music is always checked
        Pair(NavBarSection.PLAYLISTS, satunesViewModel.playlistsChecked)
    )

    NavigationBar(modifier = navigationModifier) {
        for ((navBarSection: NavBarSection, visible: Boolean) in navBarSections) {
            if (visible) {
                MediaNavBarSelection(
                    modifier = navigationItemModifier,
                    navController = navController,
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
    val navController: NavHostController = rememberNavController()
    SatunesBottomAppBar(navController = navController)
}