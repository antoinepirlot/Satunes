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

package io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.utils

import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicGenre
import io.github.antoinepirlot.satunes.database.services.data.DataManager

/**
 * @author Antoine Pirlot 11/12/2025
 */

/**
 * Get the subsonic artist matching the [id] or create a new one if it doesn't exist.
 */
internal fun getOrCreateSubsonicArtist(id: Long, title: String): SubsonicArtist {
    return DataManager.getSubsonicArtist(id = id) ?: createSubsonicArtist(id = id, title = title)
}

/**
 * Create a new artist matching data
 */
private fun createSubsonicArtist(id: Long, title: String): SubsonicArtist =
    DataManager.addArtist(artist = SubsonicArtist(subsonicId = id, title = title))

internal fun getOrCreateFolder(): Folder = DataManager.getSubsonicRootFolder() //TODO

/**
 * Get the subsonic album matching the album or creates a new one and returns it if it is not known
 */
internal fun getOrCreateSubsonicAlbum(
    id: Long,
    title: String,
    coverArtId: String?,
    artistId: Long,
    artistTitle: String
): SubsonicAlbum {
    return DataManager.getSubsonicAlbum(id = id) ?: createSubsonicAlbum(
        id = id,
        title = title,
        coverArtId = coverArtId,
        artistId = artistId,
        artistTitle = artistTitle,
        year = null
    )
}

/**
 * Create a new [SubsonicAlbum] and returns it.
 */
private fun createSubsonicAlbum(
    id: Long,
    title: String,
    coverArtId: String?,
    artistId: Long,
    artistTitle: String,
    year: Int?
): SubsonicAlbum =
    DataManager.addAlbum(
        album = SubsonicAlbum(
            subsonicId = id,
            title = title,
            coverArtId = coverArtId,
            artist = getOrCreateSubsonicArtist(id = artistId, title = artistTitle),
            //            isCompilation = false,
            year = year
        )
    )

internal fun getSubsonicGenre(): Genre =
    SubsonicGenre(subsonicId = 1, title = "UNKNOWN CLOUD GENRE") //TODO