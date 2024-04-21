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

package earth.satunes.ui.views.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import earth.satunes.R
import earth.satunes.internet.updates.UpdateAvailableStatus
import earth.satunes.internet.updates.UpdateAvailableStatus.AVAILABLE
import earth.satunes.internet.updates.UpdateAvailableStatus.CANNOT_CHECK
import earth.satunes.internet.updates.UpdateAvailableStatus.UNDEFINED
import earth.satunes.internet.updates.UpdateAvailableStatus.UP_TO_DATE
import earth.satunes.internet.updates.UpdateCheckManager
import earth.satunes.internet.updates.UpdateCheckManager.getCurrentVersion
import earth.satunes.ui.components.LoadingCircle
import earth.satunes.ui.components.buttons.updates.CheckUpdateButton
import earth.satunes.ui.components.settings.UpdateAvailable
import earth.satunes.ui.components.texts.NormalText
import earth.satunes.ui.components.texts.Title

/**
 * @author Antoine Pirlot on 11/04/2024
 */

@Composable
fun UpdatesView(
    modifier: Modifier = Modifier,
) {
    val context: Context = LocalContext.current
    val currentVersion = getCurrentVersion(context = context)
    val isCheckingUpdate: Boolean by remember { UpdateCheckManager.isCheckingUpdate }
    val updateAvailable: UpdateAvailableStatus by remember { UpdateCheckManager.updateAvailableStatus }
    Column(modifier = modifier.padding(16.dp)) {
        Title(text = stringResource(id = R.string.version), fontSize = 20.sp)
        NormalText(text = stringResource(id = R.string.current_version) + currentVersion)
        //Check update is done when pressing setting button in top app bar
        if (isCheckingUpdate) {
            LoadingCircle(modifier.padding(bottom = 16.dp))
            return
        }

        when (updateAvailable) {
            UNDEFINED, CANNOT_CHECK, UP_TO_DATE -> CheckUpdateButton()
            AVAILABLE -> UpdateAvailable()
        }
    }
}

@Preview
@Composable
fun VersionViewPreview() {
    UpdatesView()
}