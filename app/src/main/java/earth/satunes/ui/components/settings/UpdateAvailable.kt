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

package earth.satunes.ui.components.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.satunes.internet.APKDownloadStatus
import earth.satunes.internet.R
import earth.satunes.internet.UpdateCheckManager
import earth.satunes.ui.components.LoadingCircle
import earth.satunes.ui.components.buttons.updates.DownloadButton
import earth.satunes.ui.components.buttons.updates.InstallRequestButton
import earth.satunes.ui.components.buttons.updates.SeeDetailsButton
import earth.satunes.ui.components.playlist.SPACER_SIZE

/**
 * @author Antoine Pirlot on 14/04/2024
 */

@Composable
fun UpdateAvailable(
    modifier: Modifier = Modifier,
) {
    val context: Context = LocalContext.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(id = R.string.update_available))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SeeDetailsButton()
            Spacer(modifier = Modifier.size(SPACER_SIZE))
            val downloadStatus: APKDownloadStatus by remember { UpdateCheckManager.downloadStatus }
            when (downloadStatus) {
                APKDownloadStatus.CHECKING, APKDownloadStatus.DOWNLOADING -> LoadingCircle()
                APKDownloadStatus.DOWNLOADED -> InstallRequestButton()
                APKDownloadStatus.NOT_STARTED -> DownloadButton()
                else -> return
            }
        }
    }
}

@Preview
@Composable
fun UpdateAvailablePreview() {
    UpdateAvailable()
}