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

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.antoinepirlot.satunes.database.models.Media
import io.github.antoinepirlot.satunes.database.models.Music
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 27/03/2024
 */

@Serializable
@Entity(tableName = "playlists", indices = [Index(value = ["title"], unique = true)])
data class PlaylistDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id") override var id: Long,
    @ColumnInfo(name = "title") override var title: String,

) : Media {

    @Ignore
    @Transient
    override val musicMediaItemSortedMapUpdate: MutableState<Boolean>? = null // Not used

    @Ignore
    @Transient
    override val musicMediaItemSortedMap: SortedMap<Music, MediaItem>? = null // Not used

    @Ignore
    @Transient
    override val liked: MutableState<Boolean> = mutableStateOf(false)

    @Ignore
    @Transient
    override var artwork: Bitmap? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaylistDB

        return title.lowercase() == other.title.lowercase()
    }

    override fun hashCode(): Int {
        return title.lowercase().hashCode()
    }
}