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

import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlGenre
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 26/09/2025
 */
@Serializable
internal data class SubsonicAlbum(
    val id: String,
    val name: String,
    val artist: String,
    val artistId: String,
    val covertArt: String,
    val songCount: Int,
    val duration: Long,
    val playCount: Long,
    val created: String,
    val played: String,
    val userRating: Int,
//    val genres: Collection<XmlGenre>,
    val musicBrainzId: String,
    val isCompilation: Boolean,
    val sortName: String,
//    val discTitles: Collection<Any>,
//    val originalReleasesDate: Any,
//    val releaseTypes: Collection<Any>,
//    val recordLabels: Collection<Any>,
//    val moods: Collection<Any>,
    val artists: Collection<SubsonicArtist>,
    val displayArtist: String,
    val explicitStatus: String,
    val version: String
)