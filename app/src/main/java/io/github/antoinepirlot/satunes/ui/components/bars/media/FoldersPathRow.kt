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
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.bars.media

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.ui.components.buttons.folders.FolderPathButton
import kotlin.math.absoluteValue

/**
 * @author Antoine Pirlot 12/10/2025
 */
@Composable
fun FoldersPathRow(
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel = viewModel(),
    endFolder: Folder
) {
    val navController: NavController = LocalNavController.current
    val folders: Collection<Folder> = endFolder.getPathAsFolderList()
    val rowPadding: Dp = 16.dp
    Row(modifier = modifier.padding(start = rowPadding, top = rowPadding, end = rowPadding)) {
        for (folder: Folder in folders) {
            var onClick: (() -> Unit)? = null
            if (folder != endFolder) onClick = {
                goBackTo(
                    navigationViewModel = navigationViewModel,
                    navController = navController,
                    currentFolder = endFolder,
                    target = folder,
                )
            }
            FolderPathButton(folder = folder, onClick = onClick)
        }
    }
}

/**
 * Navigate back to the [target].
 * @param navController
 * @param target the folder where to go.
 */
private fun goBackTo(
    navigationViewModel: NavigationViewModel,
    navController: NavController,
    currentFolder: Folder,
    target: Folder
) {
    val distance: Int = (currentFolder.getDepth() - target.getDepth()).absoluteValue
    for (i: Int in 0..<distance) navigationViewModel.popBackStack(navController = navController)
}