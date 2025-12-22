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

package io.github.antoinepirlot.satunes.database.models.media.subsonic

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import io.github.antoinepirlot.android.utils.utils.runIOThread
import io.github.antoinepirlot.satunes.database.models.internet.ApiRequester
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist

/**
 * @author Antoine Pirlot 11/12/2025
 */
class SubsonicAlbum(
    override var subsonicId: Long,
    id: Long = subsonicId,
    title: String,
    coverArtId: String? = null,
    artist: Artist,
    isCompilation: Boolean = false,
    year: Int? = null,
    private val apiRequester: ApiRequester
) : SubsonicMedia, Album(
    id = id,
    title = title,
    coverArtId = coverArtId,
    artist = artist,
    isCompilation = isCompilation,
    year = year
) {
    /**
     * Fetch artwork from network and stores it into [artwork]
     */
    override fun loadArtwork(onDataRetrieved: (artwork: ImageBitmap?) -> Unit) {
        if (this.artwork != null)
            onDataRetrieved(this.artwork!!.applyShape().asImageBitmap())
        else
            runIOThread {
                apiRequester.getCoverArt(
                    coverArtId = this.coverArtId!!,
                    onDataRetrieved = {
                        this@SubsonicAlbum.artwork = it
                        onDataRetrieved(it?.applyShape()?.asImageBitmap())
                    }
                )
            }
    }

    override fun equals(other: Any?): Boolean {
        return if(this.javaClass == other?.javaClass) this.subsonicId == (other as SubsonicAlbum).subsonicId
        else super.equals(other)
    }

    override fun hashCode(): Int {
        return 31 * super.hashCode() + (this.subsonicId.hashCode())
    }
}