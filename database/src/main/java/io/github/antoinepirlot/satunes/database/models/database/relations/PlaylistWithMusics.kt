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

package io.github.antoinepirlot.satunes.database.models.database.relations

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.Relation
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicDB
import io.github.antoinepirlot.satunes.database.models.database.tables.MusicsPlaylistsRel
import io.github.antoinepirlot.satunes.database.models.database.tables.PlaylistDB
import io.github.antoinepirlot.satunes.database.models.media.MediaData
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author Antoine Pirlot on 27/03/2024
 */

@Serializable
internal data class PlaylistWithMusics(
    @Embedded val playlistDB: PlaylistDB,
    @Relation(
        parentColumn = "playlist_id",
        entityColumn = "music_id",
        associateBy = Junction(MusicsPlaylistsRel::class)
    )
    val musics: MutableList<MusicDB>
) : MediaData {
    @Ignore
    @Transient
    override lateinit var title: String // Not used

    @Ignore
    @Transient
    override var id: Long = playlistDB.id
}
