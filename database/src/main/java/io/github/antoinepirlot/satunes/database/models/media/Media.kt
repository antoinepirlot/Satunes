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

import io.github.antoinepirlot.satunes.database.models.DownloadStatus

/**
 * @author Antoine Pirlot on 11/07/2024
 */
interface Media : MediaData, Comparable<Media> {
    val musicCollection: Collection<Music>

    val downloadStatus: DownloadStatus

    fun isSubsonic(): Boolean = false
    fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean

    /**
     * If the media is stored on the device, it returns true.
     * If the media is subsonic and is downloaded on the phone then it returns true, false otherwise
     */
    fun isStoredLocally(): Boolean

    fun clearMusicList()

    fun contains(media: Media): Boolean

    fun addMusic(music: Music)

    fun addMusics(musics: Collection<Music>)

    fun indexOf(media: Media): Int

    fun removeMusic(music: Music)

    /**
     * Remove this media impl from storage.
     */
    fun removeFromStorage()

    fun musicCount(): Int
    fun isRootFolder(): Boolean

    fun isFolder(): Boolean
    fun isBackFolder(): Boolean
    fun isMusic(): Boolean
    fun isAlbum(): Boolean
    fun isGenre(): Boolean
    fun isArtist(): Boolean
    fun isPlaylist(): Boolean

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}