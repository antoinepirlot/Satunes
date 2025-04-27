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

package io.github.antoinepirlot.satunes.ui.views.settings.updates

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus.AVAILABLE
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus.CANNOT_CHECK
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus.UNDEFINED
import io.github.antoinepirlot.satunes.internet.updates.UpdateAvailableStatus.UP_TO_DATE
import io.github.antoinepirlot.satunes.ui.components.LoadingCircle
import io.github.antoinepirlot.satunes.ui.components.buttons.updates.CheckUpdateButton
import io.github.antoinepirlot.satunes.ui.components.settings.updates.UpdateAvailable
import io.github.antoinepirlot.satunes.ui.components.settings.updates.UpdateChannelSelection

/**
 * @author Antoine Pirlot on 11/04/2024
 */

private val SIZE = 16.dp

@RequiresApi(Build.VERSION_CODES.M)
@Composable
internal fun UpdatesSettingView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val currentVersion: String = satunesViewModel.getCurrentVersion()
    val isCheckingUpdate: Boolean = satunesViewModel.isCheckingUpdate
    val updateAvailable: UpdateAvailableStatus = satunesViewModel.updateAvailableStatus
    val scrollState: ScrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(text = stringResource(id = R.string.version))
        NormalText(text = stringResource(id = R.string.current_version) + currentVersion)
        UpdateChannelSelection()
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

            //Check update is done when pressing setting button in top app bar
            if (isCheckingUpdate) {
                Spacer(modifier = Modifier.size(SIZE)) // To align with text and not have a vertical cut
                LoadingCircle()
                return
            }

            when (updateAvailable) {
                UNDEFINED, CANNOT_CHECK, UP_TO_DATE -> {
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