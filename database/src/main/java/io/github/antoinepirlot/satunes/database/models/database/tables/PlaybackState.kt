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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.models.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * @author Antoine Pirlot 07/02/2025
 */
@Entity(
    tableName = "playback_state",
    primaryKeys = ["music_id"],
    foreignKeys = [
        ForeignKey(
            MusicDB::class,
            arrayOf("music_id"),
            arrayOf("music_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaybackState(
    //TODO remember all list (original and others) what about SortOption?
    //TODO remember using serializing? (How to avoid duplicated Music objects? listing only ids? what about process complexity?
    @ColumnInfo(name = "music_id") val musicId: Long,
    @ColumnInfo(name = "is_playing", defaultValue = "FALSE") val isPlaying: Boolean,
    @ColumnInfo(name = "duration") val duration: Long,
)