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

package earth.satunes.ui.views.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import earth.satunes.R

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