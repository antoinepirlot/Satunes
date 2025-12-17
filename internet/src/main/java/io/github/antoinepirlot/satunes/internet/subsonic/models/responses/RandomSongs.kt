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

package io.github.antoinepirlot.satunes.internet.subsonic.models.responses

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Song
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 11/12/2025
 */
@Serializable
internal data class RandomSongs(
    @SerialName(value = "song") private val songs: List<Song>
) {
    fun toSubsonicMusicCollection(subsonicApiRequester: SubsonicApiRequester): Collection<SubsonicMusic> {
        val collection: MutableCollection<SubsonicMusic> = mutableSetOf()
        for (song: Song in songs)
            collection.add(
                element = song
                    .toSubsonicMedia(subsonicApiRequester = subsonicApiRequester) as SubsonicMusic
            )
        return collection
    }
}