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

import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.services.data.DataManager

/**
 * @author Antoine Pirlot 11/12/2025
 */

/**
 * Get the artist matching the [artistId] or create a new one if it doesn't exist.
 */
internal fun getOrCreateArtist(id: Long, title: String): Artist {
    return DataManager.getArtist(id = id) ?: createArtist(id = id, title = title)
}

/**
 * Create a new artist matching data
 */
private fun createArtist(id: Long, title: String): Artist = Artist(id = id, title = title)

internal fun getOrCreateFolder(): Folder = DataManager.getSubsonicRootFolder()!! //TODO

/**
 * Get the subsonic album matching the album or creates a new one and returns it if it is not known
 */
internal fun getOrCreateAlbum(id: Long, title: String, artistId: Long, artistTitle: String): Album {
    return DataManager.getAlbum(id = id) ?: createAlbum(
        id = id,
        title = title,
        artistId = artistId,
        artistTitle = artistTitle,
        year = null
    )
}

/**
 * Create a new Album and returns it.
 */
private fun createAlbum(
    id: Long,
    title: String,
    artistId: Long,
    artistTitle: String,
    year: Int?
): Album {
    return Album(
        id = id,
        title = title,
        artist = getOrCreateArtist(id = artistId, title = artistTitle),
//            isCompilation = false,
        year = year
    )
}

internal fun getGenre(): Genre = Genre(title = "UNKNOWN CLOUD GENRE") //TODO