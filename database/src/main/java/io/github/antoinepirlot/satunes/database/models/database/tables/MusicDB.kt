/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.models.database.tables

import androidx.compose.runtime.MutableState
import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import io.github.antoinepirlot.satunes.database.exceptions.MusicNotFoundException
import io.github.antoinepirlot.satunes.database.models.Media
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.DataManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Serializable
@Entity("musics")
internal data class MusicDB(
    @PrimaryKey
    @ColumnInfo("music_id") override val id: Long,
) : Media {
    @Ignore
    @Transient
    override lateinit var title: String

    var liked: Boolean = false

    @Ignore
    @Transient
    override val musicMediaItemMap: SortedMap<Music, MediaItem>? = null

    @Ignore
    @Transient
    override val musicMediaItemMapUpdate: MutableState<Boolean>? = null

    @Ignore
    @Transient
    var music: Music? = try {
        DataManager.getMusic(musicId = this.id)
    } catch (_: MusicNotFoundException) {
        // Happens when importing playlistDB
        null
    }
}