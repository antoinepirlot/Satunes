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

package io.github.antoinepirlot.satunes.ui.views.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus.AVAILABLE
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus.CANNOT_CHECK
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus.UNDEFINED
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus.UP_TO_DATE
import io.github.antoinepirlot.satunes.ui.components.LoadingCircle
import io.github.antoinepirlot.satunes.ui.components.buttons.updates.CheckUpdateButton
import io.github.antoinepirlot.satunes.ui.components.settings.UpdateAvailable
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.components.texts.Title
import io.github.antoinepirlot.satunes.ui.local.LocalMainScope
import io.github.antoinepirlot.satunes.ui.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.ui.viewmodels.SatunesViewModel
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 11/04/2024
 */

private val PADDING = 16.dp

@RequiresApi(Build.VERSION_CODES.M)
@Composable
internal fun UpdatesSettingView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    val currentVersion: String = satunesViewModel.getCurrentVersion()
    val isCheckingUpdate: Boolean = satunesViewModel.isCheckingUpdate
    val updateAvailable: UpdateAvailableStatus = satunesViewModel.updateAvailableStatus
    val scrollState: ScrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Title(
            modifier = Modifier.padding(horizontal = PADDING),
            text = stringResource(id = R.string.version)
        )
        NormalText(
            modifier = Modifier.padding(horizontal = PADDING),
            text = stringResource(id = R.string.current_version) + currentVersion
        )
        Row(modifier = Modifier.fillMaxWidth()) {

            //Check update is done when pressing setting button in top app bar
            if (isCheckingUpdate) {
                Spacer(modifier = Modifier.size(PADDING)) // To align with text and not have a vertical cut
                LoadingCircle()
                return
            }

            when (updateAvailable) {
                UNDEFINED, CANNOT_CHECK, UP_TO_DATE -> {
                    Spacer(modifier = Modifier.size(PADDING)) // To align with text and not have a vertical cut
                    CheckUpdateButton()
                }

                AVAILABLE -> {
                    UpdateAvailable()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@Preview
@Composable
private fun VersionViewPreview() {
    UpdatesSettingView()
}