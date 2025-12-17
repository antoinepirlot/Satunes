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

import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.services.data.DataManager

/**
 * @author Antoine Pirlot 11/12/2025
 */
class SubsonicArtist(
    override var subsonicId: Long,
    id: Long = subsonicId,
    title: String,
) : SubsonicMedia, Artist(
    id = subsonicId,
    title = title,
) {
    override fun isSubsonic(): Boolean = true

    /**
     * Transform this [Artist] to [SubsonicArtist].
     * After that, this [Artist] can't no more be used
     */
    open fun toSubsonicArtist(artist: SubsonicArtist): SubsonicArtist {
        val newArtist: SubsonicArtist = SubsonicArtist(
            id = this.id,
            subsonicId = artist.subsonicId,
            title = this.title
        )
        for (music: Music in this.musicSortedSet) {
            music.updateArtist(artist = newArtist)
            music.album.updateArtist(artist = newArtist)
        }
        DataManager.removeArtist(artist = this)
        DataManager.addArtist(artist = newArtist)
        return newArtist
    }
}