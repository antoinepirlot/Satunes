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

package io.github.antoinepirlot.satunes.ui.components.bars.media

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.database.models.media.Folder
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
    val folders: List<Folder> = endFolder.getPathAsFolderList()
    val rowPadding: Dp = 16.dp
    val spacerSize: Dp = rowPadding
    val lazyListState: LazyListState = rememberLazyListState()

    LazyRow(
        modifier = modifier.padding(top = rowPadding),
        state = lazyListState
    ) {
        items(
            items = folders,
            key = { it.javaClass.name + '-' + it.id!! }
        ) { targetFolder: Folder ->
            var onClick: (() -> Unit)? = null
            if (targetFolder != endFolder) onClick = {
                navigationViewModel.navigate(
                    navController = navController,
                    media = targetFolder
                )
            }
            if (targetFolder == folders.first())
                Spacer(modifier = Modifier.size(size = spacerSize))
            FolderPathButton(folder = targetFolder, onClick = onClick)
            if (targetFolder == folders.last())
                Spacer(modifier = Modifier.size(size = spacerSize))
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (folders.isNotEmpty()) lazyListState.animateScrollToItem(index = folders.lastIndex)
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