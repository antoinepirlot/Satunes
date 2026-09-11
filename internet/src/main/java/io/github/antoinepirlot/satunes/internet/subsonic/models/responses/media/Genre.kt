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
 */

/**
 * @file Genre
 * @author Antoine Pirlot (PirloTech)
 * @since 15/07/2026
 */

package io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.utils.getOrCreateSubsonicGenre
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Genre(
    @SerialName(value = "value") val title: String? = null,
    @SerialName(value = "name") val title2: String? = null,
    @SerialName(value = "song") val songs: Collection<Song> = listOf()
) : SubsonicData {
    //No id as it is identified by its name so id = title
    override fun toSubsonicMedia(subsonicApiRequester: SubsonicApiRequester): SubsonicMedia =
        getOrCreateSubsonicGenre(
            subsonicApiRequester = subsonicApiRequester,
            title = this.getRightTitle(),
            songs = songs
        )

    internal fun getRightTitle(): String =
        if (this.songs.isNotEmpty()) this.songs.first().genres.first().getRightTitle() else this.title ?: this.title2!!

}