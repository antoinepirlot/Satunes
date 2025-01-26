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

package io.github.antoinepirlot.satunes.ui.components.settings.updates

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.internet.updates.APKDownloadStatus
import io.github.antoinepirlot.satunes.ui.components.LoadingCircle
import io.github.antoinepirlot.satunes.ui.components.buttons.updates.DownloadButton
import io.github.antoinepirlot.satunes.ui.components.buttons.updates.InstallRequestButton
import io.github.antoinepirlot.satunes.ui.components.buttons.updates.SeeDetailsButton
import io.github.antoinepirlot.satunes.internet.R as RInternet

/**
 * @author Antoine Pirlot on 14/04/2024
 */

private val SPACER_SIZE = 10.dp

@RequiresApi(Build.VERSION_CODES.M)
@Composable
internal fun UpdateAvailable(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        NormalText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = RInternet.string.update_available)
        )
        val scrollState: ScrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(16.dp)) // To align with text and not have a vertical cut
            SeeDetailsButton(text = stringResource(id = R.string.see_on_fdroid), onFdroid = true)
            Spacer(modifier = Modifier.size(SPACER_SIZE))
            SeeDetailsButton()
            Spacer(modifier = Modifier.size(SPACER_SIZE))
            val downloadStatus: APKDownloadStatus = satunesViewModel.downloadStatus
            when (downloadStatus) {
                APKDownloadStatus.CHECKING, APKDownloadStatus.DOWNLOADING -> LoadingCircle()
                APKDownloadStatus.DOWNLOADED -> InstallRequestButton()
                APKDownloadStatus.NOT_STARTED -> DownloadButton()
                else -> return
            }
            Spacer(modifier = Modifier.size(16.dp)) // To align with text and not have a vertical cut
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@Preview
@Composable
private fun UpdateAvailablePreview() {
    UpdateAvailable()
}