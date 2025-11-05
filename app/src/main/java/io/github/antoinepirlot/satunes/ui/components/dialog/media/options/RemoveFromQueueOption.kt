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

package io.github.antoinepirlot.satunes.ui.components.dialog.media.options

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.ui.components.dialog.options.DialogOption
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 11/08/2024
 */

@Composable
internal fun RemoveFromQueueOption(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    mediaImpl: MediaImpl,
    onDismissRequest: () -> Unit,
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current

    DialogOption(
        modifier = modifier,
        onClick = {
            playbackViewModel.removeFromQueue(
                scope = scope,
                snackBarHostState = snackBarHostState,
                mediaImpl = mediaImpl
            )
            onDismissRequest()
        },
        jetpackLibsIcons = JetpackLibsIcons.REMOVE_FROM_QUEUE,
        text = stringResource(id = R.string.remove_from_queue_option)
    )
}

@Preview
@Composable
private fun RemoveFromQueueOptionPreview() {
    RemoveFromQueueOption(mediaImpl = Folder(title = "Folder title"), onDismissRequest = {})
}