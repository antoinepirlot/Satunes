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
    var musicList: List<Music>
    var mediaItemList: MutableList<MediaItem>
    var isShuffle: MutableState<Boolean> =
        mutableStateOf(PlaybackController.DEFAULT_IS_SHUFFLE)


    init {
        this.musicList = musicMediaItemSortedMap.keys.toList()
        this.mediaItemList = musicMediaItemSortedMap.values.toMutableList()
        this.originalMusicMediaItemMap = musicMediaItemSortedMap.toSortedMap()
    }

    /**
     * Shuffle the playlist
     */
    fun activateShuffle() {
        this.musicList = this.musicList.shuffled()
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
        this.musicList = this.originalMusicMediaItemMap.keys.toList()
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

    @Suppress("NAME_SHADOWING")
    fun getMediaItems(fromIndex: Int, toIndex: Int): List<MediaItem> {
        val toIndex = if (toIndex > this.mediaItemList.size) this.mediaItemList.size else toIndex
        val fromIndex = if (fromIndex < 0) 0 else fromIndex
        if (fromIndex >= toIndex) {
            throw IllegalArgumentException("The fromIndex has to be lower than toIndex")
        }

        val toReturn: MutableList<MediaItem> = mutableListOf()
        for (i: Int in fromIndex..<toIndex) {
            toReturn.add(this.mediaItemList[i])
        }
        return toReturn
    }
}