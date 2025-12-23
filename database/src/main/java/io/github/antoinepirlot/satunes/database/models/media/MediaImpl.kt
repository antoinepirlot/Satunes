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
import io.github.antoinepirlot.android.utils.utils.toCircularBitmap
import io.github.antoinepirlot.satunes.database.models.DownloadStatus
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
) : Media {
    protected val logger: Logger? = Logger.getLogger()
    override var downloadStatus: DownloadStatus by mutableStateOf(value = DownloadStatus.NOT_DOWNLOADED)
        protected set

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

    var artwork: Bitmap? = null
        internal set

    /**
     * Declared in concrete classes (Music and Folder)
     */
    protected open var addedDate: Date? = null

    @get:Synchronized
    protected open val musicSortedSet: SortedSet<Music> = sortedSetOf()

    @get:Synchronized
    override val musicCollection: Collection<Music> = mutableStateListOf()

    override fun isEmpty(): Boolean {
        return this.musicSortedSet.isEmpty()
    }

    override fun isNotEmpty(): Boolean {
        return this.musicSortedSet.isNotEmpty()
    }

    override fun isStoredLocally(): Boolean = downloadStatus == DownloadStatus.DOWNLOADED

    @Synchronized
    override fun clearMusicList() {
        this.musicSortedSet.clear()
        this.musicCollection as MutableList<Music>
        (this.musicCollection as MutableList<Music>).clear()
    }

    override fun contains(media: Media): Boolean {
        //TODO hashing collision using the set collection.
        return if (media.isMusic())
            this.musicCollection.contains(element = media)
        else if (media.isFolder())
            this.musicCollection.containsAll(elements = (media as Folder).getAllMusic())
        else this.musicCollection.containsAll(elements = media.musicCollection)
    }

    @Synchronized
    override fun addMusic(music: Music) {
        if (!this.contains(media = music)) {
            this.musicSortedSet.add(element = music)
            (this.musicCollection as MutableList<Music>).add(element = music)
            (this.musicCollection as MutableList<Music>).sort()
        }
    }

    @Synchronized
    override fun addMusics(musics: Collection<Music>) {
        for(music: Music in musics) this.addMusic(music = music)
    }

    @Synchronized
    override fun removeMusic(music: Music) {
        if (this.contains(media = music)) {
            this.musicSortedSet.remove(music)
            (this.musicCollection as MutableList<Music>).remove(element = music)
        }
    }

    /**
     * Stores this [SubsonicMusic] into Satunes's storage for offline usage.
     * If it is already stored, do nothing
     */
    override fun download() {
        if (this.isStoredLocally()) return
        this.downloadStatus = DownloadStatus.DOWNLOADING
        TODO("Saving in cache is not yet implemented.")
    }

    override fun removeFromStorage() {
        if (!this.isStoredLocally()) return
        TODO("Remove from storage is not yet implemented")
    }

    /**
     * Apply circle shape if user enabled it.
     *
     * @return the shaped [Bitmap]
     */
    protected fun Bitmap.applyShape(): Bitmap {
        return if (SettingsManager.artworkCircleShape.value) this.toCircularBitmap() else this
    }

    override fun compareTo(other: Media): Int {
        other as MediaImpl //Ensure no other class is added in the future that extends Media and is not MediaImpl
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

    override fun musicCount(): Int = this.musicSortedSet.size
    override fun isRootFolder(): Boolean = false

    override fun isFolder(): Boolean = false
    override fun isBackFolder(): Boolean = false
    override fun isMusic(): Boolean = false
    override fun isAlbum(): Boolean = false
    override fun isGenre(): Boolean = false
    override fun isArtist(): Boolean = false
    override fun isPlaylist(): Boolean = false
}