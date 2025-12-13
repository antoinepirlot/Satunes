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

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.database.models.comparators.StringComparator
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import java.util.Date
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 29/03/2024
 */
abstract class MediaImpl(
    override val id: Long,
    title: String
) : Media, Comparable<MediaImpl> {
    protected val _logger: Logger? = Logger.getLogger()
    protected var isDownloaded: Boolean = !this.isSubsonic()
        set(value) {
            if (!this.isSubsonic())
                throw IllegalStateException("Can't change value of isDownloaded for a local media.")
            field = value
        }

    /**
     * Title of the media. If this is a music and the [SettingsManager.isMusicTitleDisplayName] is
     * true, then it means the user selected the file as title and the file's extension must be removed.
     *
     * So if the user has selected the option and if the file's name is: "example.mp3" the music's title
     * will be "example" otherwise if there's no music's tag "title", then it will be "example.mp3".
     */
    override var title: String by mutableStateOf(
        value =
            if (!this.isMusic() || !SettingsManager.isMusicTitleDisplayName) title
            else title.split(".").first()
    )

    var artwork: Bitmap? by mutableStateOf(null)
        internal set

    /**
     * Declared in concrete classes (Music and Folder)
     */
    protected open var addedDate: Date? = null

    protected open val musicSortedSet: SortedSet<Music> = sortedSetOf()
    val musicList: List<Music> = mutableStateListOf()

    open fun isEmpty(): Boolean {
        return this.musicSortedSet.isEmpty()
    }

    open fun isNotEmpty(): Boolean {
        return this.musicSortedSet.isNotEmpty()
    }

    open fun isSubsonic(): Boolean = false

    /**
     * If the media is stored on the device, it returns true.
     * If the media is subsonic and is downloaded on the phone then it returns true, false otherwise
     */
    fun isStoredLocally(): Boolean = isDownloaded

    fun clearMusicList() {
        this.musicSortedSet.clear()
        this.musicList as MutableList
        this.musicList.clear()
    }

    fun contains(mediaImpl: MediaImpl): Boolean {
        return if (mediaImpl.isMusic())
            this.musicList.contains(mediaImpl)
        else if (mediaImpl.isFolder())
            this.musicList.containsAll(elements = (mediaImpl as Folder).getAllMusic())
        else this.musicList.containsAll(elements = mediaImpl.musicList)
    }

    open fun addMusic(music: Music) {
        if (!this.musicSortedSet.contains(element = music)) {
            this.musicSortedSet.add(element = music)
            this.musicList as MutableList
            this.musicList.add(element = music)
            this.musicList.sort()
        }
    }

    open fun addMusics(musics: Collection<Music>) {
        this.musicSortedSet.addAll(musics)
        this.musicList as MutableList
        this.musicList.clear()
        this.musicList.addAll(this.musicSortedSet)
    }

    open fun removeMusic(music: Music) {
        if (this.musicSortedSet.contains(element = music)) {
            this.musicSortedSet.remove(music)
            this.musicList as MutableList
            this.musicList.remove(element = music)
        }
    }

    /**
     * Stores this [SubsonicMusic] into Satunes's storage for offline usage.
     * If it is already stored, do nothing
     */
    fun download() {
        if (this.isStoredLocally()) return
        TODO("Saving in cache is not yet implemented.")
    }

    /**
     * Remove this media impl from storage.
     */
    fun removeFromStorage() {
        if (!this.isStoredLocally()) return
        TODO("Remove from storage is not yet implemented")
    }

    override fun compareTo(other: MediaImpl): Int {
        if (this == other) return 0
        var compared: Int = StringComparator.compare(o1 = this.title, o2 = other.title)
        if (compared == 0 && this.javaClass != other.javaClass) {
            compared = if (this.isMusic()) -1
            else if (this.isAlbum()) if (other.isMusic()) 1 else -1
            else if (this.isArtist()) if (other.isMusic() || other.isAlbum()) 1 else -1
            else if (this.isGenre()) if (other.isFolder() || other.isPlaylist()) -1 else 1
            else if (this.isPlaylist()) if (!other.isPlaylist() && other.isFolder()) -1 else 1
            else 1
        }
        return compared
    }

    open fun musicCount(): Int = this.musicSortedSet.size
    open fun isRootFolder(): Boolean = false

    open fun isFolder(): Boolean = false
    open fun isBackFolder(): Boolean = false
    open fun isMusic(): Boolean = false
    open fun isAlbum(): Boolean = false
    open fun isGenre(): Boolean = false
    open fun isArtist(): Boolean = false
    open fun isPlaylist(): Boolean = false
}