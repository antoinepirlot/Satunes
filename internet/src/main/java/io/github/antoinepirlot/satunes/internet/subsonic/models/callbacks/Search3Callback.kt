/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Album
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Artist
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Song
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot 13/12/2025
 */
internal class Search3Callback(
    subsonicApiRequester: SubsonicApiRequester,
    onSucceed: (() -> Unit)? = null,
    onFinished: (() -> Unit)? = null,
    onDataRetrieved: (Collection<SubsonicMedia>) -> Unit,
    onError: ((Error?) -> Unit)? = null,
) : SubsonicCallback<Collection<SubsonicMedia>>(
    subsonicApiRequester = subsonicApiRequester,
    onDataRetrieved = onDataRetrieved,
    onSucceed = onSucceed,
    onFinished = onFinished,
    onError = onError
) {
    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call, response)
        if (this.processData())
            this.onSucceed?.invoke()
        this.onFinished?.invoke()
    }

    override fun processData(): Boolean {
        if (!super.processData()) return false
        val mediaSet: MutableSet<SubsonicMedia> = mutableSetOf()
        this.subsonicResponse!!.search3?.apply {
            this.artists?.forEach { artist: Artist ->
                mediaSet.add(element = artist.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester))
            }
            this.albums?.forEach { album: Album ->
                mediaSet.add(element = album.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester))
            }
            this.songs?.forEach { song: Song ->
                mediaSet.add(element = song.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester))
            }
        }
        this.onDataRetrieved(mediaSet.sorted())
        return true
    }
}