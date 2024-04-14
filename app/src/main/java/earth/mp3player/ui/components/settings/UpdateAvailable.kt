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

package earth.mp3player.ui.components.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.internet.R
import earth.mp3player.internet.UpdateAvailableStatus
import earth.mp3player.internet.UpdateManager
import earth.mp3player.ui.components.settings.utils.openUrl

/**
 * @author Antoine Pirlot on 14/04/2024
 */

@Composable
fun UpdateAvailable(
    modifier: Modifier = Modifier,
) {
    val context: Context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.update_available))
        Button(onClick = {
            openUrl(
                context = context,
                url = UpdateAvailableStatus.AVAILABLE.updateLink!!
            )
        }) {
            Text(text = stringResource(id = R.string.see_details))
        }
        Button(onClick = { UpdateManager.downloadUpdateApk(context = context) }) {
            Text(text = stringResource(id = R.string.download_update))
        }
    }
}

@Preview
@Composable
fun UpdateAvailablePreview() {
    UpdateAvailable()
}