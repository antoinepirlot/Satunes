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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3player.R
import earth.mp3player.internet.UpdateManager
import earth.mp3player.ui.components.texts.Title

/**
 * @author Antoine Pirlot on 11/04/2024
 */

@Composable
fun VersionView(
    modifier: Modifier = Modifier,
) {
    val context: Context = LocalContext.current
    val currentVersion = getCurrentVersion(context = context)
    Column(modifier = modifier.padding(16.dp)) {
        Title(text = stringResource(id = R.string.version))
        Text(text = stringResource(id = R.string.current_version) + currentVersion)
        if (isUpdateAvailable(context = context)) {
            Text(text = stringResource(id = R.string.update_available))
        } else {
            Text(text = stringResource(id = R.string.no_update))
        }
    }
}

private fun getCurrentVersion(context: Context): String {
    return context.packageManager.getPackageInfo(context.packageName, 0).versionName
}

private fun isUpdateAvailable(context: Context): Boolean {
    return when (UpdateManager.checkUpdate(context = context)) {
        1 -> true
        else -> false
    }
}

@Preview
@Composable
fun VersionViewPreview() {
    VersionView()
}