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

import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.media.SubsonicAlbum
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.SubsonicResponse
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot 26/09/2025
 */
internal class GetArtistCallback(
    subsonicApiRequester: SubsonicApiRequester,
    onSucceed: (() -> Unit)? = null,
    onError: ((Error?) -> Unit)? = null,
) : SubsonicCallback(
    subsonicApiRequester = subsonicApiRequester,
    onSucceed = onSucceed,
    onError = onError,
) {
    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call, response)
//        if(!this.hasReceivedData()) return
        val response: SubsonicResponse = this.response!!
        if (!response.hasArtist()) throw IllegalStateException("No Artist received.")
        for(album: SubsonicAlbum in response.artist?.subsonicAlbums!!)
            DataManager.addAlbum(album = album.toAlbum())
//        this.dataProcessed()
        this.onSucceed?.invoke()
    }
}