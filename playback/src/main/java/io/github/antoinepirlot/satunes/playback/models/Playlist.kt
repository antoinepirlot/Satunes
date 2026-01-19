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

package io.github.antoinepirlot.satunes.playback.models

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.media3.common.MediaItem
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.playback.exceptions.AlreadyInPlaybackException

/**
 * @author Antoine Pirlot on 18/02/24
 */

internal class Playlist(musics: Collection<Music>) : Iterable<Music> {
    private val _originalMusicMediaItemMap: MutableMap<Music, MediaItem> = mutableMapOf()
    private val _subsonicMusics: MutableSet<SubsonicMusic> = mutableSetOf()
    val musicList: SnapshotStateList<Music> = SnapshotStateList()
    val mediaItemList: MutableList<MediaItem> = mutableListOf()
    internal var isShuffle: Boolean = false
        private set
    private val _logger: Logger? = Logger.getLogger()


    init {
        musics.forEach { music: Music -> this.add(music = music) }
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
        this.clear()
        if (musicMoving != null) this.add(music = musicMoving)
        this.addAll(musics = shuffledMusicList)
        this.isShuffle = true
    }

    /**
     * Undo shuffle, set to the original playlistDB
     */
    fun undoShuffle() {
        this.clear()
        this._originalMusicMediaItemMap.forEach { (music: Music, _) ->
            this.add(music = music)
        }
        this.isShuffle = false
    }

    private fun clear() {
        this.musicList.clear()
        this.mediaItemList.clear()
        this._subsonicMusics.clear()
    }

    fun getMusicIndex(music: Music): Int = this.musicList.indexOf(music)

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
    fun musicCount(): Int = this.musicList.size

    fun lastIndex(): Int = this.musicList.lastIndex

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

    fun getMediaItem(music: Music): MediaItem = this._originalMusicMediaItemMap[music]!!

    fun addToQueue(music: Music) {
        checkMusicIsInPlaylist(music = music)
        this.add(music = music)
    }

    fun addNext(index: Int, music: Music) {
        checkMusicIsInPlaylist(music = music)
        this.addAt(index = index, music = music)
    }

    private fun add(music: Music) {
        if (!this.contains(music = music)) {
            //It's always a new one when getting media item
            val mediaItem: MediaItem = music.mediaItem
            this._originalMusicMediaItemMap[music] = mediaItem
            this.musicList.add(music)
            this.mediaItemList.add(mediaItem)
        }
    }

    private fun addAll(musics: Collection<Music>) = musics.forEach { this.add(music = it) }

    private fun addAt(index: Int, music: Music) {
        if (!this.contains(music = music)) {
            //It's always a new one when getting media item
            val mediaItem: MediaItem = music.mediaItem
            this._originalMusicMediaItemMap[music] = mediaItem
            this.musicList.add(index = index, element = music)
            this.mediaItemList.add(index = index, element = mediaItem)
        }
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

    fun isMusicInQueue(music: Music): Boolean = this.contains(music = music)

    private fun contains(music: Music): Boolean =
        this._originalMusicMediaItemMap.containsKey(key = music)

    private fun removeAt(index: Int) {
        val removedMusic: Music = this.musicList.removeAt(index = index)
        this._subsonicMusics.remove(element = removedMusic)
        this._originalMusicMediaItemMap.remove(key = removedMusic)
    }

    /**
     * Removes music from queue and returns its old index position.
     *
     * @return old index or -1 if the music has not been found.
     */
    fun removeFromQueue(music: Music): Int {
        val oldIndex: Int = this.getMusicIndex(music = music)
        this.removeAt(index = oldIndex)
        return oldIndex
    }

    /**
     * Checks if this playlist is the same as [_originalMusicMediaItemMap]'s keys.
     * True means it's the same playlist otherwise false.
     *
     * @return true if [_originalMusicMediaItemMap] contains all [musics]'s musics as keys false otherwise
     */
    internal fun hasPlaylistMusicCollection(musics: Collection<Music>): Boolean {
        if (this.musicCount() != musics.size) return false
        for (music in musics)
            if (!this.contains(music = music)) return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Playlist) return false

        return _originalMusicMediaItemMap == other._originalMusicMediaItemMap
    }

    override fun hashCode(): Int = _originalMusicMediaItemMap.hashCode()

    override fun iterator(): Iterator<Music> = this.musicList.iterator()

    fun updateMediaItem(media: SubsonicMusic) {
        this._originalMusicMediaItemMap[media] = media.mediaItem
    }

    fun updateMediaItems(medias: Collection<SubsonicMusic>) {
        medias.forEach { this.updateMediaItem(media = it) }
    }
}