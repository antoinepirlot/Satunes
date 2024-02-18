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
    var mediaItemList: List<MediaItem>
    var indexMusicPlaying: Int = PlaybackController.DEFAULT_MUSIC_PLAYING_INDEX
    var isShuffle: MutableState<Boolean> =
        mutableStateOf(PlaybackController.DEFAULT_IS_SHUFFLE)


    init {
        this.musicList = musicMediaItemSortedMap.keys.toList()
        this.mediaItemList = musicMediaItemSortedMap.values.toList()
        this.originalMusicMediaItemMap = musicMediaItemSortedMap.toSortedMap()
    }

    /**
     * Shuffle the playing and if music playing is not null, returns the new index of the music playing
     *
     * @param musicPlaying the music that is playing, null if no music is playing
     *
     * @return the new index of the music playing, null if music playing is null
     */
    fun shuffle(musicPlaying: Music? = null): Int? {
        if (this.isShuffle.value) {
            // Deactivate Shuffle
            this.musicList = this.originalMusicMediaItemMap.keys.toList()
            this.mediaItemList = this.originalMusicMediaItemMap.values.toList()
        } else {
            // Activate Shuffle
            this.musicList = this.musicList.shuffled()
            this.mediaItemList = this.mediaItemList.shuffled()
        }
        this.isShuffle.value = !this.isShuffle.value
        if (musicPlaying != null) {
            val musicIndex = this.musicList.indexOf(musicPlaying)
            if (musicIndex < 0) {
                throw IllegalArgumentException("This music is not present in music list")
            }
            return musicIndex
        }
        //No music is playing
        return null
    }
}