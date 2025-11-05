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
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.database.models.media

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.antoinepirlot.satunes.database.models.comparators.StringComparator
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import java.util.Date
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 29/03/2024
 */
abstract class MediaImpl(
    id: Long,
    title: String
) : Media, Comparable<MediaImpl> {
    protected val _logger: SatunesLogger? = SatunesLogger.getLogger()

    override var id: Long = id
        internal set

    /**
     * Title of the media. If this is a music and the [SettingsManager.isMusicTitleDisplayName] is
     * true, then it means the user selected the file as title and the file's extension must be removed.
     *
     * So if the user has selected the option and if the file's name is: "example.mp3" the music's title
     * will be "example" otherwise if there's no music's tag "title", then it will be "example.mp3".
     */
    override var title: String by mutableStateOf(
        value =
            if (this !is Music || !SettingsManager.isMusicTitleDisplayName) title
            else title.split(".").first()
    )

    var artwork: Bitmap? by mutableStateOf(null)
        internal set

    /**
     * Declared in concrete classes (Music and Folder)
     */
    protected open var addedDate: Date? = null

    protected open val musicSortedSet: SortedSet<Music> = sortedSetOf()
    val musicSetUpdated: MutableState<Boolean> = mutableStateOf(false)

    open fun isEmpty(): Boolean {
        return this.musicSortedSet.isEmpty()
    }

    open fun isNotEmpty(): Boolean {
        return this.musicSortedSet.isNotEmpty()
    }

    fun getMusicSet(): Set<Music> {
        return this.musicSortedSet
    }

    fun clearMusicSet(triggerUpdate: Boolean = true) {
        this.musicSortedSet.clear()
        if (triggerUpdate) this.listUpdated()
    }

    fun contains(mediaImpl: MediaImpl): Boolean {
        return when (mediaImpl) {
            is Music -> this.getMusicSet().contains(mediaImpl)
            is Folder -> this.getMusicSet().containsAll(elements = mediaImpl.getAllMusic())
            else -> this.getMusicSet().containsAll(elements = mediaImpl.getMusicSet())
        }
    }

    open fun addMusic(music: Music, triggerUpdate: Boolean = true) {
        if (!this.musicSortedSet.contains(element = music)) {
            this.musicSortedSet.add(element = music)
            if (triggerUpdate) this.listUpdated()
        }
    }

    open fun addMusics(musics: Collection<Music>, triggerUpdate: Boolean = true) {
        this.musicSortedSet.addAll(musics)
        if (triggerUpdate) this.listUpdated()
    }

    open fun removeMusic(music: Music, triggerUpdate: Boolean = true) {
        if (this.musicSortedSet.contains(element = music)) {
            this.musicSortedSet.remove(music)
            this.listUpdated()
        }
    }

    protected fun listUpdated() {
        if (this.musicSetUpdated.value) this.musicSetUpdated.value = false
        this.musicSetUpdated.value = true
    }

    override fun compareTo(other: MediaImpl): Int {
        if (this == other) return 0
        var compared: Int = StringComparator.compare(o1 = this.title, o2 = other.title)
        if (compared == 0 && this.javaClass != other.javaClass) {
            compared = when (this) {
                is Music -> -1
                is Album -> if (other is Music) 1 else -1
                is Artist -> if (other is Music || other is Album) 1 else -1
                is Genre -> if (other is Folder || other is Playlist) -1 else 1
                is Playlist -> if (other !is Playlist && other is Folder) -1 else 1
                else -> 1
            }
        }
        return compared
    }

    open fun musicCount(): Int = this.musicSortedSet.size
}