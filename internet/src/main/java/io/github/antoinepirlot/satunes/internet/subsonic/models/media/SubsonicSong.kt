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

package io.github.antoinepirlot.satunes.internet.subsonic.models.media

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 27/09/2025
 */

@RequiresApi(Build.VERSION_CODES.M)
@Serializable
@SerialName("song")
internal data class SubsonicSong(
    val id: String,
    val title: String,
    val artistId: String,
    val albumId: String,
    val genre: String,
    val path: String,
    val size: Int,
    val track: Int,
    val duration: Long,
    val mediaType: String,
) {
    fun toMusic(subsonicApiRequester: SubsonicApiRequester): Music {
        if(mediaType != SubsonicApiRequester.SONG_MEDIA_TYPE)
            throw IllegalStateException("Can't create not song to music.")
        val genre: Genre = Genre(title = genre)
        DataManager.addGenre(genre = genre)
        return DataManager.getSubsonicMusic(subsonicId = id)?: Music(
            id = -1, //TODO
            subsonicId = id,
            title = title,
            absolutePath = "", //TODO
            duration = duration,
            size = size,
            cdTrackNumber = track,
            addedDateMs = 0,//TODO,
            folder = DataManager.getSubsonicRootFolder()!!, //TODO give param of folder
            artist = DataManager.getArtist(subsonicId = artistId)!!,
            album = DataManager.getAlbum(subsonicId = albumId)!!,
            genre = DataManager.getGenre(title = genre.title)!!,
            uri = "${subsonicApiRequester.url}/${subsonicApiRequester.inUrlMandatoryParams}&id=$id".toUri(),
            displayName = "" //TODO
        )
    }
}