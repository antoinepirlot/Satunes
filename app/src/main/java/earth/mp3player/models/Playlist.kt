/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 */

package earth.mp3player.models

import androidx.media3.common.MediaItem
import java.util.SortedMap

class Playlist(
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

    /**
     * Return the number of music into the playlist
     */
    fun musicCount(): Int {
        return this.musicList.size
    }

    fun lastIndex(): Int {
        return this.musicList.lastIndex
    }

    @Suppress("NAME_SHADOWING")
    fun getMediaItems(fromIndex: Int, toIndex: Int): List<MediaItem> {
        val toIndex = if (toIndex > this.musicList.lastIndex) this.musicList.lastIndex else toIndex
        val fromIndex = if (fromIndex < 0) 0 else fromIndex
        if (fromIndex > toIndex) {
            throw IllegalArgumentException("The fromIndex has to be lower than toIndex")
        }

        val toReturn: MutableList<MediaItem> = mutableListOf()
        for (i: Int in fromIndex..toIndex) {
            toReturn.add(this.mediaItemList[i])
        }
        return toReturn
    }
}