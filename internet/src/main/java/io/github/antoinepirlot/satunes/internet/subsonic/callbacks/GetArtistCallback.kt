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

package io.github.antoinepirlot.satunes.internet.subsonic.callbacks

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.SubsonicState
import io.github.antoinepirlot.satunes.internet.subsonic.models.media.SubsonicArtist
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlAlbum
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlMedia
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlObject
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot 26/09/2025
 */
@RequiresApi(Build.VERSION_CODES.M)
class GetArtistCallback(
    subsonicApiRequester: SubsonicApiRequester,
    onSucceed: (() -> Unit) ? = null,
    val artist: SubsonicArtist
): SubsonicCallback(
    subsonicApiRequester = subsonicApiRequester,
    onSucceed = onSucceed
) {
    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call, response)
        this.checkIfReceivedData()
        for(xmlObject: XmlObject in SubsonicState.DATA_RECEIVED.dataReceived) {
            if(!xmlObject.isHeader()) {
                if(!xmlObject.isMedia()) throw IllegalStateException("No XmlMedia.")
                xmlObject as XmlMedia
                if(!xmlObject.isAlbum()) throw IllegalStateException("No XmlAlbum.")
                xmlObject as XmlAlbum
                DataManager.addAlbum(album = xmlObject.toSubsonicAlbum(artist = artist))
            }
        }
        this.dataProcessed()
    }
}