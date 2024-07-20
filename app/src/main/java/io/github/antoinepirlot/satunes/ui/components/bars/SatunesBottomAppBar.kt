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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.ui.ScreenSizes
import io.github.antoinepirlot.satunes.ui.states.SatunesUiState
import io.github.antoinepirlot.satunes.ui.viewmodels.SatunesViewModel

/**
 * @author Antoine Pirlot on 03/02/24
 */

@Composable
internal fun SatunesBottomAppBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    satunesViewModel: SatunesViewModel = viewModel()
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val navigationModifier: Modifier =
        if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) modifier.fillMaxHeight(0.11f) else modifier
    val navigationItemModifier: Modifier =
        if (screenWidthDp < ScreenSizes.VERY_VERY_SMALL) Modifier.size(16.dp) else Modifier
    NavigationBar(modifier = navigationModifier) {
        if (satunesUiState.foldersChecked) {
            MediaNavBarSelection(
                modifier = navigationItemModifier,
                navController = navController,
                navBarSection = NavBarSection.FOLDERS
            )
        }

        if (satunesUiState.artistsChecked) {
            MediaNavBarSelection(
                modifier = navigationItemModifier,
                navController = navController,
                navBarSection = NavBarSection.ARTISTS
            )
        }

        if (satunesUiState.albumsChecked) {
            MediaNavBarSelection(
                modifier = navigationItemModifier,
                navController = navController,
                navBarSection = NavBarSection.ALBUMS
            )
        }
        if (satunesUiState.genresChecked) {
            MediaNavBarSelection(
                modifier = navigationItemModifier,
                navController = navController,
                navBarSection = NavBarSection.GENRES
            )
        }

        MediaNavBarSelection(
            modifier = navigationItemModifier,
            navController = navController,
            navBarSection = NavBarSection.MUSICS
        )

        if (satunesUiState.playlistsChecked) {
            MediaNavBarSelection(
                modifier = navigationItemModifier,
                navController = navController,
                navBarSection = NavBarSection.PLAYLISTS
            )
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