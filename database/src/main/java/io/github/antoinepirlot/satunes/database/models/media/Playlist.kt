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

package io.github.antoinepirlot.satunes.database.models.media

import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 11/07/2024
 */
class Playlist(
    id: Long, // Managed by Database
    title: String
) : MediaImpl(id = id, title = title) {

    override fun addMusic(music: Music, triggerUpdate: Boolean) {
        super.addMusic(music, triggerUpdate)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val order: Long =
                    DatabaseManager.getInstance().getOrder(playlist = this@Playlist, music = music)
                music.setOrderInPlaylist(
                    playlist = this@Playlist,
                    order = order
                )
            } catch (e: Throwable) {
                _logger?.severe(e.message)
                throw e
            }
        }
    }

    override fun addMusics(musics: Collection<Music>) {
        for (music: Music in musics) {
            this.addMusic(music = music, triggerUpdate = false)
        }
    }

    override fun removeMusic(music: Music) {
        super.removeMusic(music)
        music.removeOrderInPlaylist(playlist = this)
    }

    override fun isPlaylist(): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Playlist

        return title.lowercase() == other.title.lowercase()
    }

    override fun hashCode(): Int {
        return title.lowercase().hashCode()
    }

    override fun compareTo(other: MediaImpl): Int {
        if (this.title == LIKES_PLAYLIST_TITLE || other.title == LIKES_PLAYLIST_TITLE) {
            if (this.title == other.title) {
                return 0
            }
            if (this.title == LIKES_PLAYLIST_TITLE) {
                return -1
            }
            return 1
        }

        return super.compareTo(other)
    }

    override fun toString(): String {
        return this.title
    }
}