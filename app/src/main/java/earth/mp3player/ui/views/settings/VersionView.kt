/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.ui.views.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import earth.mp3player.R
import earth.mp3player.internet.UpdateAvailableStatus
import earth.mp3player.internet.UpdateAvailableStatus.AVAILABLE
import earth.mp3player.internet.UpdateAvailableStatus.CANNOT_CHECK
import earth.mp3player.internet.UpdateAvailableStatus.UP_TO_DATE
import earth.mp3player.internet.UpdateManager
import earth.mp3player.internet.UpdateManager.getCurrentVersion
import earth.mp3player.ui.components.LoadingCircle
import earth.mp3player.ui.components.texts.Title
import earth.mp3player.internet.R as RInternet

/**
 * @author Antoine Pirlot on 11/04/2024
 */

@Composable
fun VersionView(
    modifier: Modifier = Modifier,
) {
    val context: Context = LocalContext.current
    val currentVersion = getCurrentVersion(context = context)
    val isCheckingUpdate: Boolean by remember { UpdateManager.isCheckingUpdate }
    val updateAvailable: UpdateAvailableStatus by remember { UpdateManager.updateAvailable }
    Column(modifier = modifier.padding(16.dp)) {
        Title(text = stringResource(id = R.string.version), fontSize = 20.sp)
        Text(text = stringResource(id = R.string.current_version) + currentVersion)
        //Check update is done when pressing setting button in top app bar
        if (isCheckingUpdate) {
            LoadingCircle(modifier.padding(bottom = 16.dp))
        } else {
            when (updateAvailable) {
                CANNOT_CHECK -> Text(text = stringResource(id = RInternet.string.cannot_check_update))
                AVAILABLE -> Text(text = stringResource(id = RInternet.string.update_available))
                UP_TO_DATE -> Text(text = stringResource(id = RInternet.string.no_update))
            }
        }
    }
}

@Preview
@Composable
fun VersionViewPreview() {
    VersionView()
}