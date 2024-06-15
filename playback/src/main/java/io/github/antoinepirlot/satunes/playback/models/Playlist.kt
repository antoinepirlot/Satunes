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

package io.github.antoinepirlot.satunes.playback.models

import androidx.media3.common.MediaItem
import io.github.antoinepirlot.satunes.database.models.Music
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 18/02/24
 */

internal class Playlist(
    musicMediaItemSortedMap: SortedMap<Music, MediaItem>,
) {
    private val originalMusicMediaItemMap: SortedMap<Music, MediaItem>
    var musicList: MutableList<Music>
    var mediaItemList: MutableList<MediaItem>


    init {
        this.musicList = musicMediaItemSortedMap.keys.toMutableList()
        this.mediaItemList = musicMediaItemSortedMap.values.toMutableList()
        this.originalMusicMediaItemMap = musicMediaItemSortedMap.toSortedMap()
    }

    /**
     * Shuffle the playlist
     * @param musicIndex the music index of the music to place at the index 0
     */
    fun shuffle(musicIndex: Int = -1) {
        if (musicIndex > this.musicList.lastIndex) {
            throw IllegalArgumentException("The music index is greater than last index of the list")
        }
        var musicMoving: Music? = null
        if (musicIndex >= 0) {
            musicMoving = this.musicList.removeAt(musicIndex)
        }

        if (musicMoving != null) {
            val oldMusicList: MutableList<Music> = this.musicList
            this.musicList = mutableListOf()
            this.musicList.add(musicMoving)
            this.musicList.addAll(oldMusicList.shuffled())
        } else {
            this.musicList = this.musicList.shuffled().toMutableList()
        }
        this.mediaItemList = mutableListOf()
        this.musicList.forEach { music: Music ->
            this.mediaItemList.add(music.mediaItem)
        }
    }

    /**
     * Undo shuffle, set to the original playlist
     */
    fun undoShuffle() {
        this.musicList = this.originalMusicMediaItemMap.keys.toMutableList()
        this.mediaItemList = this.originalMusicMediaItemMap.values.toMutableList()
    }

    fun getMusicIndex(music: Music): Int {
        return this.musicList.indexOf(music)
    }

    fun getMusic(musicIndex: Int): Music {
        return this.musicList[musicIndex]
    }

    /**
     * Return the number of music into the playlist
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
     * If no toIndex is specified then it goes to the last index of the playlist.
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
            throw IllegalArgumentException("The fromIndex has to be lower than toIndex")
        }
        return mediaItemList.subList(fromIndex = fromIndex, toIndex = toIndex + 1)
    }
}