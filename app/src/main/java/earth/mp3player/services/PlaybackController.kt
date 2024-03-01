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

 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.services

import android.content.ComponentName
import android.content.Context
import android.os.Environment
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import earth.mp3player.models.Music
import earth.mp3player.models.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.SortedMap

class PlaybackController private constructor(
    context: Context,
    sessionToken: SessionToken,
    musicMediaItemSortedMap: SortedMap<Music, MediaItem>,
) {
    private lateinit var mediaController: MediaController

    private var playlist: Playlist
    private var musicPlayingIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX
    private var isEnded: Boolean = DEFAULT_IS_ENDED
    private var isUpdatingPosition: Boolean = DEFAULT_IS_UPDATING_POSITION


    // Mutable var are used in ui, it needs to be recomposed
    // I use mutable to avoid using function with multiples params like to add listener
    var musicPlaying: MutableState<Music?> = mutableStateOf(DEFAULT_MUSIC_PLAYING)
    var isPlaying: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_PLAYING_VALUE)
    var repeatMode: MutableState<Int> = mutableIntStateOf(DEFAULT_REPEAT_MODE)
    val isShuffle: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_SHUFFLE)
    var hasNext: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_NEXT)
    var hasPrevious: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_PREVIOUS)
    var isLoaded: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_LOADED)
    var currentPositionProgression: MutableFloatState =
        mutableFloatStateOf(DEFAULT_CURRENT_POSITION_PROGRESSION)

    init {
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            this.mediaController = controllerFuture.get()
        }, ContextCompat.getMainExecutor(context))
        this.playlist = Playlist(musicMediaItemSortedMap = musicMediaItemSortedMap)
        //PlaybackService.mediaSession.player.mediaMetadata.
    }

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_ENDED) {
                isPlaying.value = false
                isEnded = true
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            isPlaying.value = playWhenReady
            if (playWhenReady) {
                isEnded = DEFAULT_IS_ENDED
                updateCurrentPosition()
            }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            currentPositionProgression.floatValue =
                newPosition.positionMs.toFloat() / musicPlaying.value!!.duration
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (musicPlaying.value == null || playlist.musicCount() <= 1) {
                //Fix issue while loading for the first time
                return
            }
            if (mediaItem != musicPlaying.value!!.mediaItem) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK
                    || reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
                ) {
                    val previousMusic: Music? =
                        if (musicPlayingIndex != DEFAULT_MUSIC_PLAYING_INDEX) playlist.musicList[musicPlayingIndex - 1]
                        else null

                    if (previousMusic == null || mediaItem != previousMusic.mediaItem) {
                        // Next button has been clicked
                        next(repeatMode = repeatMode.value)
                    } else {
                        // Previous button has been clicked
                        previous(repeatMode = repeatMode.value)
                    }
                }
            }
            musicPlaying.value = playlist.musicList[musicPlayingIndex]
            updateHasNext()
            updateHasPrevious()
            mediaController.play()
            isPlaying.value = !DEFAULT_IS_PLAYING_VALUE
            isEnded = DEFAULT_IS_ENDED
        }

        private fun next(repeatMode: Int) {
            // The next button has been clicked
            when (repeatMode) {
                Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ONE -> {
                    musicPlayingIndex++
                }

                Player.REPEAT_MODE_ALL -> {
                    if (musicPlayingIndex + 1 == playlist.musicList.size) {
                        musicPlayingIndex = 0
                    } else {
                        musicPlayingIndex++
                    }
                }
            }
        }

        private fun previous(repeatMode: Int) {
            when (repeatMode) {
                Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ONE -> {
                    musicPlayingIndex--
                }

                Player.REPEAT_MODE_ALL -> {
                    if (musicPlayingIndex == DEFAULT_MUSIC_PLAYING_INDEX) {
                        musicPlayingIndex = playlist.musicList.lastIndex
                    } else {
                        musicPlayingIndex--
                    }
                }
            }
        }
    }


    private fun updateHasPrevious() {
        this.hasPrevious.value = musicPlaying.value != this.playlist.musicList.last()
    }

    private fun updateHasNext() {
        this.hasNext.value = musicPlaying.value != playlist.musicList.last()
    }

    companion object {

        private const val DEFAULT_MUSIC_PLAYING_INDEX: Int = 0
        private const val DEFAULT_IS_UPDATING_POSITION: Boolean = false
        private const val DEFAULT_IS_ENDED: Boolean = false

        val ROOT_PATH: String = Environment.getExternalStorageDirectory().path
        val DEFAULT_MUSIC_PLAYING = null

        const val DEFAULT_IS_PLAYING_VALUE: Boolean = false
        const val DEFAULT_REPEAT_MODE: Int = Player.REPEAT_MODE_OFF
        const val DEFAULT_IS_SHUFFLE: Boolean = false
        const val DEFAULT_HAS_NEXT: Boolean = false
        const val DEFAULT_HAS_PREVIOUS: Boolean = false
        const val DEFAULT_IS_LOADED: Boolean = false
        const val DEFAULT_CURRENT_POSITION_PROGRESSION: Float = 0f

        private lateinit var instance: PlaybackController

        /**
         * Return only one instance of MediaController. If there's no instance already created
         * you have to send a context to the constructor otherwise send null.
         *
         * @return the instance of MediaController
         */
        fun getInstance(): PlaybackController {
            if (!Companion::instance.isInitialized) {
                throw IllegalStateException("The PlayBackController has not been initialized")
            }
            return instance
        }

        fun initInstance(
            context: Context,
            musicMediaItemSortedMap: SortedMap<Music, MediaItem>
        ): PlaybackController {
            if (!Companion::instance.isInitialized) {
                val sessionToken =
                    SessionToken(context, ComponentName(context, PlaybackService::class.java))
                instance = PlaybackController(
                    context = context,
                    sessionToken = sessionToken,
                    musicMediaItemSortedMap = musicMediaItemSortedMap,
                )
            }
            return getInstance()
        }
    }

    fun start(musicToPlay: Music? = null) {
        if (!this.isLoaded.value) {
            throw IllegalStateException("The playlist has not been loaded, you can't play music")
        }

        when (musicToPlay) {
            null -> {
                //Play from the beginning
                this.musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
                this.musicPlaying.value = this.playlist.musicList[musicPlayingIndex]
            }

            this.musicPlaying.value -> {
                // The music playing is the same as the music to play, nothing is done
                return
            }

            else -> {
                // The music to play has to be played
                for (i: Int in this.playlist.musicList.indices) {
                    val music = this.playlist.musicList[i]
                    if (musicToPlay == music) {
                        this.musicPlaying.value = music
                        this.musicPlayingIndex = i
                        break
                    }
                }
            }
        }

        this.mediaController.seekTo(this.musicPlayingIndex, 0)
        this.mediaController.play()
        this.updateHasNext()
        this.updateHasPrevious()
    }

    fun playPause() {
        if (this.isPlaying.value) {
            this.mediaController.pause()
            return
        } else {
            if (this.isEnded) {
                this.start()
                this.isEnded = false
            } else {
                this.mediaController.play()
            }
        }
    }

    fun playNext() {
        if (playlist.musicCount() <= 1) {
            return
        }
        this.mediaController.seekToNext()
    }

    fun playPrevious() {
        if (playlist.musicCount() <= 1) {
            return
        }
        mediaController.seekToPrevious()
    }

    fun seekTo(positionMs: Long) {
        this.mediaController.seekTo(positionMs)
    }

    fun seekTo(positionPercentage: Float) {
        if (this.musicPlaying.value == null || !this.isPlaying.value || this.isEnded) {
            throw IllegalStateException("Impossible to seek while no music is playing")
        }
        val maxPosition: Long = this.musicPlaying.value!!.duration
        val newPosition: Long = (positionPercentage * maxPosition).toLong()
        this.seekTo(positionMs = newPosition)
    }

    /**
     * Launch a coroutine where the currentPositionProgression is updated every 1 second.
     * If this function is already running, just return by using isUpdatingPosition.
     */
    private fun updateCurrentPosition() {
        if (this.isUpdatingPosition || musicPlaying.value == null) {
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            isUpdatingPosition = !DEFAULT_IS_UPDATING_POSITION
            while (isPlaying.value) {
                val maxPosition: Long = musicPlaying.value!!.duration
                val newPosition: Long = mediaController.currentPosition
                currentPositionProgression.floatValue =
                    newPosition.toFloat() / maxPosition.toFloat()
                delay(1000) // Wait one second to avoid refreshing all the time
            }
            if (isEnded) {
                // It means the music has reached the end of playlist and the music is finished
                currentPositionProgression.floatValue = 1f
            }
            isUpdatingPosition = DEFAULT_IS_UPDATING_POSITION
        }
    }

    /**
     * Add all music from musicMap to the mediaController in the same order.
     * If the shuffle mode is true then shuffle the playlist
     *
     * @param musicMediaItemSortedMap the music map to load if null use the musicQueueToPlay instead
     * @param shuffleMode indicate if the playlist has to be started in shuffle mode by default false
     *
     */
    fun loadMusic(
        musicMediaItemSortedMap: SortedMap<Music, MediaItem>,
        shuffleMode: Boolean = false
    ) {
        this.playlist = Playlist(musicMediaItemSortedMap = musicMediaItemSortedMap)
        if (shuffleMode) {
            this.playlist.shuffle()
        }
        this.mediaController.clearMediaItems()
        this.mediaController.addMediaItems(this.playlist.mediaItemList)
        this.mediaController.addListener(listener)
        this.mediaController.prepare()

        this.isLoaded.value = true
        this.isShuffle.value = shuffleMode
    }

    fun switchShuffleMode() {
        this.isShuffle.value = !this.isShuffle.value
        if (this.playlist.musicCount() <= 1) {
            return
        }
        if (!this.isShuffle.value) {
            // Deactivate shuffle
            this.undoShuffle()
        } else {
            // Activate shuffle mode
            this.shuffle()
        }
    }

    /**
     * Move music playing to the first index and remove other
     * the music playing has to take its original place.
     */
    private fun shuffle() {
        if (this.musicPlaying.value == null) {
            // No music playing
            this.playlist.shuffle()
            return
        } else {
            this.playlist.shuffle(musicIndex = this.musicPlayingIndex)
        }
        //A music is playing
        this.mediaController.moveMediaItem(
            this.mediaController.currentMediaItemIndex,
            DEFAULT_MUSIC_PLAYING_INDEX
        )
        val fromIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX + 1
        val toIndex: Int = this.playlist.musicCount() - 1
        this.mediaController.replaceMediaItems(
            fromIndex,
            toIndex,
            this.playlist.getMediaItems(fromIndex = fromIndex, toIndex = toIndex)
        )
        this.musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
    }

    /**
     * Restore the original playlist
     *
     */
    private fun undoShuffle() {
        this.playlist.undoShuffle()
        if (this.musicPlaying.value == null) {
            // No music playing
            this.mediaController.clearMediaItems()
            this.mediaController.addMediaItems(this.playlist.mediaItemList)
            return
        }

        val oldMusicPlayingIndex = this.musicPlayingIndex
        this.musicPlayingIndex = this.playlist.getMusicIndex(this.musicPlaying.value!!)
        val lastIndex: Int = this.playlist.lastIndex()

        when (this.musicPlayingIndex) {
            DEFAULT_MUSIC_PLAYING_INDEX -> {
                // Music playing is at the index 0, replace index 1 to last index
                // The original place is the first
                this.mediaController.moveMediaItem(
                    oldMusicPlayingIndex,
                    DEFAULT_MUSIC_PLAYING_INDEX
                )
                val fromIndex: Int = this.musicPlayingIndex + 1
                this.mediaController.replaceMediaItems(
                    fromIndex,
                    lastIndex + 1,
                    this.playlist.getMediaItems(fromIndex = fromIndex, toIndex = lastIndex)
                )
            }

            lastIndex -> {
                // The music is at the last index, replace from index 0 to last index -1
                this.mediaController.moveMediaItem(
                    oldMusicPlayingIndex,
                    this.musicPlayingIndex + 1
                )
                this.mediaController.replaceMediaItems(
                    DEFAULT_MUSIC_PLAYING_INDEX,
                    lastIndex,
                    this.playlist.getMediaItems(
                        fromIndex = DEFAULT_MUSIC_PLAYING_INDEX,
                        toIndex = lastIndex - 1
                    )
                )
            }

            else -> {
                // Music playing is not at the easy position, replace before music playing and after
                this.mediaController.moveMediaItem(
                    oldMusicPlayingIndex,
                    this.musicPlayingIndex
                )
                this.mediaController.replaceMediaItems(
                    0,
                    this.musicPlayingIndex,
                    this.playlist.getMediaItems(fromIndex = 0, toIndex = this.musicPlayingIndex - 1)
                )
                this.mediaController.replaceMediaItems(
                    this.musicPlayingIndex + 1,
                    lastIndex + 1,
                    this.playlist.getMediaItems(
                        fromIndex = this.musicPlayingIndex + 1,
                        toIndex = lastIndex
                    )
                )
            }
        }
    }

    fun switchRepeatMode() {
        when (this.repeatMode.value) {
            Player.REPEAT_MODE_OFF -> {
                this.repeatMode.value = Player.REPEAT_MODE_ALL
            }

            Player.REPEAT_MODE_ALL -> {
                this.repeatMode.value = Player.REPEAT_MODE_ONE
            }

            Player.REPEAT_MODE_ONE -> {
                this.repeatMode.value = Player.REPEAT_MODE_OFF
            }
        }
        this.mediaController.repeatMode = this.repeatMode.value
    }
}