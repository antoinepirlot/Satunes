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

package io.github.antoinepirlot.satunes.playback.models

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.media3.common.MediaItem
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.playback.exceptions.AlreadyInPlaybackException
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger

/**
 * @author Antoine Pirlot on 18/02/24
 */

//TODO refactor and clarify the usage of original map
internal class Playlist(musics: Collection<Music>) {
    private val originalMusicMediaItemMap: MutableMap<Music, MediaItem> = mutableMapOf()
    val musicList: SnapshotStateList<Music> = SnapshotStateList()
    val mediaItemList: MutableList<MediaItem> = mutableListOf()
    internal var isShuffle: Boolean = false
        private set
    private val _logger: SatunesLogger? = SatunesLogger.getLogger()


    init {
        musics.forEach { music: Music ->
            this.musicList.add(element = music)
            this.mediaItemList.add(element = music.mediaItem)
            this.originalMusicMediaItemMap[music] = music.mediaItem
        }
    }

    /**
     * Shuffle the playlistDB
     * @param musicIndex the music index of the music to place at the index 0
     */
    fun shuffle(musicIndex: Int = -1) {
        if (musicIndex > this.musicList.lastIndex) {
            val message = "The music index is greater than last index of the list"
            _logger?.severe(message)
            throw IllegalArgumentException(message)
        }
        var musicMoving: Music? = null
        if (musicIndex >= 0) {
            musicMoving = this.musicList.removeAt(musicIndex)
        }

        val shuffledMusicList: List<Music> = this.musicList.shuffled()
        this.musicList.clear()
        if (musicMoving != null) this.musicList.add(musicMoving)
        this.musicList.addAll(shuffledMusicList)
        this.mediaItemList.clear()
        this.musicList.forEach { music: Music -> this.mediaItemList.add(music.mediaItem) }
        this.isShuffle = true
    }

    /**
     * Undo shuffle, set to the original playlistDB
     */
    fun undoShuffle() {
        this.musicList.clear()
        this.mediaItemList.clear()
        this.originalMusicMediaItemMap.forEach { (music: Music, mediaItem: MediaItem) ->
            this.musicList.add(music)
            this.mediaItemList.add(mediaItem)
        }
        this.isShuffle = false
    }

    fun getMusicIndex(music: Music): Int {
        return this.musicList.indexOf(music)
    }

    fun getMusic(musicIndex: Int): Music {
        try {
            return this.musicList[musicIndex]
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }

    /**
     * Return the number of music into the playlistDB
     */
    fun musicCount(): Int {
        return this.musicList.size
    }

    fun lastIndex(): Int {
        return this.musicList.lastIndex
    }

    /**
     * Get media items list of media items between fromIndex and toIndex included.
     *
     * If toIndex is greater than the last index of the music list, then it's replaced by last index
     * If fromIndex is less than 0, then it's replaced by 0.
     *
     * If no fromIndex is specified then fromIndex is 0
     * If no toIndex is specified then it goes to the last index of the playlistDB.
     *
     * @param fromIndex the first music index to get
     * @param toIndex the last music index to get (included)
     *
     * @throws IllegalArgumentException if fromIndex is greater than toIndex
     *
     * @return a list of media items fromIndex toIndex included.
     */
    @Suppress("NAME_SHADOWING")
    fun getMediaItems(fromIndex: Int = 0, toIndex: Int = lastIndex()): List<MediaItem> {
        val toIndex = if (toIndex > this.musicList.lastIndex) this.musicList.lastIndex else toIndex
        val fromIndex = if (fromIndex < 0) 0 else fromIndex
        if (fromIndex > toIndex) {
            val message = "The fromIndex has to be lower than toIndex"
            _logger?.severe(message)
            throw IllegalArgumentException(message)
        }
        return mediaItemList.subList(fromIndex = fromIndex, toIndex = toIndex + 1)
    }

    fun addToQueue(music: Music) {
        checkMusicIsInPlaylist(music = music)
        this.originalMusicMediaItemMap[music] = music.mediaItem
        this.musicList.add(music)
        this.mediaItemList.add(music.mediaItem)
    }

    fun addNext(index: Int, music: Music) {
        checkMusicIsInPlaylist(music = music)
        this.originalMusicMediaItemMap[music] = music.mediaItem
        this.musicList.add(index = index, element = music)
        this.mediaItemList.add(index = index, element = music.mediaItem)
    }

    private fun checkMusicIsInPlaylist(music: Music) {
        if (isMusicInQueue(music = music)) {
            _logger?.severe("this.originalMusicMediaItemMap[music] != null")
            throw AlreadyInPlaybackException()
        }
    }

    /**
     * Move music to next to the current music
     *
     * @param music the music to move
     */
    fun moveMusic(music: Music, oldIndex: Int, newIndex: Int) {
        this.musicList.removeAt(oldIndex)
        this.musicList.add(index = newIndex, element = music)
        this.mediaItemList.removeAt(oldIndex)
        this.mediaItemList.add(index = newIndex, element = music.mediaItem)
    }

    fun isMusicInQueue(music: Music): Boolean {
        return this.musicList.contains(music) //TODO optimize by using map to access music from queue
    }

    /**
     * Removes music from queue and returns its old index position.
     *
     * @return old index or -1 if the music has not been found.
     */
    fun removeFromQueue(music: Music): Int {
        val oldIndex: Int = this.getMusicIndex(music = music)
        this.musicList.remove(element = music)
        return oldIndex
    }

    /**
     * Checks if this playlist is the same as [originalMusicMediaItemMap]'s keys.
     * True means it's the same playlist otherwise false.
     *
     * @return true if [originalMusicMediaItemMap] contains all [musics]'s musics as keys false otherwise
     */
    internal fun hasPlaylistMusicCollection(musics: Collection<Music>): Boolean {
        if (this.musicCount() != musics.size) return false
        for (music in musics)
            if (!this.originalMusicMediaItemMap.contains(key = music)) return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Playlist) return false

        if (originalMusicMediaItemMap != other.originalMusicMediaItemMap) return false

        return true
    }

    override fun hashCode(): Int {
        return originalMusicMediaItemMap.hashCode()
    }
}