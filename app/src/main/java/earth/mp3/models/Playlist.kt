package earth.mp3.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import earth.mp3.services.PlaybackController
import java.util.SortedMap

class Playlist(
    musicMediaItemSortedMap: SortedMap<Music, MediaItem>,
) {
    private val originalMusicMediaItemMap: SortedMap<Music, MediaItem>
    var musicList: MutableList<Music>
    var mediaItemList: MutableList<MediaItem>
    var isShuffle: MutableState<Boolean> =
        mutableStateOf(PlaybackController.DEFAULT_IS_SHUFFLE)


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
        this.isShuffle.value = true
    }

    /**
     * Undo shuffle, set to the original playlist
     */
    fun undoShuffle() {
        this.musicList = this.originalMusicMediaItemMap.keys.toMutableList()
        this.mediaItemList = this.originalMusicMediaItemMap.values.toMutableList()
        this.isShuffle.value = false
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