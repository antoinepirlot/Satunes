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

package earth.mp3player.ui.views.main.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import earth.mp3player.R

/**
 * @author Antoine Pirlot on 11/04/2024
 */

/**
 * Return the root folder name: 0 -> This device, else -> External Storage: name
 */
@Composable
fun getRootFolderName(title: String): String {
    return when (title) {
        "0" -> stringResource(id = R.string.this_device)

        else -> "${stringResource(id = R.string.external_storage)}: ${title}"
    }
}