/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

/**
 * @file SubsonicAllGenresListView
 * @author Antoine Pirlot (PirloTech)
 * @since 15/07/2026
 */

package io.github.antoinepirlot.satunes.ui.views.media.genre.subsonic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel

@Composable
fun SubsonicAllGenresListView(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    subsonicViewModel: SubsonicViewModel = viewModel(),
) {
    LaunchedEffect(key1 = Unit) {
//        TODO subsonicViewModel.loadGenres()
        dataViewModel.loadMediaImplList(collection = dataViewModel.getGenreSet())
    }
}