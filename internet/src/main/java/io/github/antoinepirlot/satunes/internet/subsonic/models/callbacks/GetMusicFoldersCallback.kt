/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 *
 */

package io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.media.SubsonicFolder
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.SubsonicResponse
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot 26/09/2025
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class GetMusicFoldersCallback(
    subsonicApiRequester: SubsonicApiRequester,
    onSucceed: (() -> Unit)? = null
) : SubsonicCallback(
    subsonicApiRequester = subsonicApiRequester, onSucceed = onSucceed
) {
    companion object {
        private const val SUBSONIC_FOLDER_TITLE = "Cloud" //TODO make it dynamic by the app's language
    }

    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call, response)
        if(!this.hasReceivedData()) return
        val response: SubsonicResponse = this.getSubsonicResponse()
        var subsonicRootFolder: Folder? = DataManager.getSubsonicRootFolder()
        if(subsonicRootFolder == null) {
            subsonicRootFolder = Folder(subsonicId = SUBSONIC_FOLDER_TITLE, title = SUBSONIC_FOLDER_TITLE) //Use subsonicId for this one to consider it as subsonic one
            DataManager.addFolder(subsonicRootFolder)
        }

        for (subsonicFolder: SubsonicFolder in response.getAllMusicFolders())
            DataManager.addFolder(folder = Folder(title = subsonicFolder.name, parentFolder = subsonicRootFolder))
        this.dataProcessed()
        this.onSucceed?.invoke()
    }
}