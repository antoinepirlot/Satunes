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
import earth.mp3player.internet.APKDownloadStatus
import earth.mp3player.internet.R
import earth.mp3player.internet.UpdateManager
import earth.mp3player.ui.components.LoadingCircle
import earth.mp3player.ui.components.buttons.updates.DownloadButton
import earth.mp3player.ui.components.buttons.updates.InstallRequestButton
import earth.mp3player.ui.components.buttons.updates.SeeDetailsButton
import earth.mp3player.ui.components.playlist.SPACER_SIZE

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
            val downloadStatus: APKDownloadStatus by remember { UpdateManager.downloadStatus }
            var message: String? = null
            when (downloadStatus) {
                APKDownloadStatus.CHECKING -> {
                    message = stringResource(id = R.string.download_checking)
                    LoadingCircle()
                }

                APKDownloadStatus.DOWNLOADED -> {
                    message = stringResource(id = R.string.downloaded)
                    InstallRequestButton()
                }

                APKDownloadStatus.DOWNLOADING -> {
                    message = stringResource(id = R.string.downloading)
                    LoadingCircle()
                }

                APKDownloadStatus.NOT_STARTED -> DownloadButton()
                APKDownloadStatus.NOT_FOUND -> message =
                    stringResource(id = R.string.download_not_found)

                APKDownloadStatus.FAILED -> message = stringResource(id = R.string.download_failed)
            }
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview
@Composable
fun UpdateAvailablePreview() {
    UpdateAvailable()
}