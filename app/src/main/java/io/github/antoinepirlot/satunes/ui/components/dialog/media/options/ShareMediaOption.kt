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

package io.github.antoinepirlot.satunes.ui.components.dialog.media.options

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.dialog.options.DialogOption
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 09/09/2024
 */

@Composable
fun ShareMediaOption(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    media: MediaImpl
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    val isSharingLoading: Boolean = dataViewModel.isSharingLoading

    DialogOption(
        modifier = modifier,
        onClick = {
            dataViewModel.share(
                scope = scope,
                snackBarHostState = snackBarHostState,
                media = media
            )
        },
        icon = SatunesIcons.SHARE,
        text = stringResource(if (isSharingLoading) R.string.sharing_loading else R.string.share_button_content),
        isLoading = isSharingLoading
    )
}