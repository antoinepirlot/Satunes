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

package io.github.antoinepirlot.satunes.ui.views.media.folder

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.router.utils.openMediaFromFolder
import io.github.antoinepirlot.satunes.ui.components.bars.FolderPath
import io.github.antoinepirlot.satunes.ui.components.bars.bottom.ExtraButtonList
import io.github.antoinepirlot.satunes.ui.views.media.MediaListView

/**
 * @author Antoine Pirlot on 01/04/2024
 */


@Composable
internal fun FolderView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    folder: Folder,
) {
    val navController: NavHostController = LocalNavController.current

    if (folder.isNotEmpty())
        satunesViewModel.replaceExtraButtons(extraButtons = {
            ExtraButtonList(
                musicSet = folder.getAllMusic(),
                mediaImplSet = null
            )
        })
    else
        satunesViewModel.clearExtraButtons()

    Column(modifier = modifier) {
        FolderPath(folder)
        MediaListView(
            mediaImplCollection = folder.getSubFolderListWithMusics(),
            openMedia = { clickedMediaImpl: MediaImpl ->
                openMediaFromFolder(
                    media = clickedMediaImpl,
                    playbackViewModel = playbackViewModel,
                    navController = navController
                )
            },
            emptyViewText = stringResource(id = R.string.no_music)
        )
    }
}

@Preview
@Composable
private fun FolderViewPreview() {
    FolderView(folder = Folder(title = "Folder title"))
}