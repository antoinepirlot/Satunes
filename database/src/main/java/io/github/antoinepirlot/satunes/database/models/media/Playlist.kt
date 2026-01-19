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

import io.github.antoinepirlot.satunes.database.models.SystemPlaylists
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 11/07/2024
 */
open class Playlist(
    id: Long?,
    title: String,
) : MediaImpl(id = id, title = title) {

    val systemPlaylists: SystemPlaylists? = SystemPlaylists.get(title = title)

    override var title: String = title
        get() {
            return if (this.isSystemPlaylist()) this.systemPlaylists!!.title
            else field
        }
        set(value) {
            if (!this.isSystemPlaylist())
                field = value
        }

    /**
     * Add music into [musicSortedSet] and [musicCollection]
     * but if the music is [SubsonicMusic] it is ignored.
     *
     * @param music a [Music] to add into [musicSortedSet] and [musicCollection]
     */
    override fun addMusic(music: Music) {
        super.addMusic(music)
        if (this.isSubsonic()) return // in that case it only depends on server's data
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val order: Long =
                    DatabaseManager.getInstance().getOrder(playlist = this@Playlist, music = music)
                music.setOrderInPlaylist(
                    playlist = this@Playlist,
                    order = order
                )
            } catch (e: Throwable) {
                logger?.severe(e.message)
                throw e
            }
        }
    }

    /**
     * Add musics into [musicSortedSet] and [musicCollection]
     * but if a music is [SubsonicMusic] it is ignored.
     *
     * @param musics a [Collection] of [Music] to add into [musicSortedSet] and [musicCollection].
     */
    override fun addMusics(musics: Collection<Music>) {
        for (music: Music in musics)
            this.addMusic(music = music)
    }

    override fun removeMusic(music: Music) {
        super.removeMusic(music)
        music.removeOrderInPlaylist(playlist = this)
    }

    override fun isPlaylist(): Boolean = true

    fun isSystemPlaylist(): Boolean = this.systemPlaylists != null

    fun isLikesPlaylist(): Boolean = this.title == SystemPlaylists.LIKES.title

    fun isDownloadsPlaylist(): Boolean = this.title == SystemPlaylists.DOWNLOADS.title

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Playlist

        return title.equals(other.title, ignoreCase = true)
    }

    override fun hashCode(): Int {
        return title.lowercase().hashCode()
    }

    override fun compareTo(other: Media): Int {
        if (this.isSystemPlaylist() || other.isPlaylist() && (other as Playlist).isSystemPlaylist()) {
            if (this.title == other.title) {
                return 0
            }
            if (this.isSystemPlaylist() && other.isPlaylist() && (other as Playlist).isSystemPlaylist()) {
                return this.title.compareTo(other = other.title)
            }
            return 1
        }

        return super.compareTo(other)
    }

    override fun toString(): String {
        return this.title
    }
}