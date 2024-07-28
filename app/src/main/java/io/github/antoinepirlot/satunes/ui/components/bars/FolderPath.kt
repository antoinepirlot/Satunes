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

package io.github.antoinepirlot.satunes.ui.components.bars

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.ui.components.texts.Title
import io.github.antoinepirlot.satunes.ui.utils.getRootFolderName

@Composable
internal fun FolderPath(folder: Folder) {
    if (folder.parentFolder == null) {
        Title(
            text = '/' + getRootFolderName(title = folder.title),
            fontSize = 20.sp,
            maxLines = 2
        )
    } else {
        val allPath: MutableList<String> = folder.absolutePath.split("/").toMutableList()
        // Wrong issue. Remove first is available before API 35, you can ignore error from Android Studio.
        allPath.removeFirst()
        allPath[0] = getRootFolderName(title = allPath[0])
        var path = ""
        for (s: String in allPath) {
            path += "/${s}"
        }
        Title(text = path, fontSize = 20.sp, maxLines = 2)
    }
}