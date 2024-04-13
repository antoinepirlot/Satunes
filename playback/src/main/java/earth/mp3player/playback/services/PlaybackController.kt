/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.playback.services

import android.content.ComponentName
import android.content.Context
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
import earth.mp3player.database.models.Music
import earth.mp3player.database.services.DataLoader
import earth.mp3player.database.services.DataManager
import earth.mp3player.playback.models.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 31/01/24
 */

class PlaybackController private constructor(
    context: Context,
    sessionToken: SessionToken,
    musicMediaItemSortedMap: SortedMap<Music, MediaItem>,
) {
    internal lateinit var mediaController: MediaController

    var playlist: Playlist
        internal set

    internal var musicPlayingIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX
    internal var isEnded: Boolean = DEFAULT_IS_ENDED
    internal var isUpdatingPosition: Boolean = DEFAULT_IS_UPDATING_POSITION

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

    private var listener: Player.Listener = PlaybackListener()

    init {
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            this.mediaController = controllerFuture.get()
        }, ContextCompat.getMainExecutor(context))

        this.playlist = Playlist(musicMediaItemSortedMap = musicMediaItemSortedMap)
    }

    companion object {
        internal const val DEFAULT_MUSIC_PLAYING_INDEX: Int = 0
        internal const val DEFAULT_IS_UPDATING_POSITION: Boolean = false
        internal const val DEFAULT_IS_ENDED: Boolean = false

        const val DEFAULT_IS_PLAYING_VALUE: Boolean = false
        const val DEFAULT_REPEAT_MODE: Int = Player.REPEAT_MODE_OFF
        const val DEFAULT_IS_SHUFFLE: Boolean = false
        const val DEFAULT_HAS_NEXT: Boolean = false
        const val DEFAULT_HAS_PREVIOUS: Boolean = false
        const val DEFAULT_IS_LOADED: Boolean = false
        const val DEFAULT_CURRENT_POSITION_PROGRESSION: Float = 0f

        val DEFAULT_MUSIC_PLAYING = null

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

        fun initInstance(context: Context, listener: Player.Listener? = null): PlaybackController {
            if (!Companion::instance.isInitialized) {
                val sessionToken =
                    SessionToken(
                        context.applicationContext,
                        ComponentName(context, PlaybackService::class.java)
                    )

                instance = PlaybackController(
                    context = context.applicationContext,
                    sessionToken = sessionToken,
                    musicMediaItemSortedMap = DataManager.musicMediaItemSortedMap,
                )
            } else if (listener != null) {
                while (!instance::mediaController.isInitialized) {
                    // Wait it is initializing
                }
                val wasPlaying: Boolean = instance.isPlaying.value
                if (instance.isPlaying.value) {
                    instance.pause()
                }
                instance.mediaController.removeListener(instance.listener)
                instance.mediaController.addListener(listener)
                instance.mediaController.prepare()
                if (wasPlaying) {
                    instance.play()
                }
            }

            instance.listener = listener ?: instance.listener

            if (!DataLoader.isLoaded && !DataLoader.isLoading.value) {
                DataLoader.loadAllData(context.applicationContext)
            }

            return getInstance()
        }
    }

    /**
     * Start the playback.
     *
     * If the music to play is null, then play the first music of the playlist, otherwise play the
     * music to play.
     *
     * If music to play is the music playing, then do nothing.
     *
     * @param musicToPlay the music to play if it's not null, by default it's null.
     */
    fun start(musicToPlay: Music? = null) {
        if (!isLoaded.value) {
            throw IllegalStateException("The playlist has not been loaded, you can't play music")
        }
        when (musicToPlay) {
            null -> {
                //Keep it first to prevent the first playing is always null and so... app crash
                //Play from the beginning
                musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
            }

            this.musicPlaying.value -> {
                // The music playing is the same as the music to play, nothing is done
                return
            }

            else -> {
                // The music to play has to be played
                musicPlayingIndex = playlist.getMusicIndex(music = musicToPlay)

            }
        }
        musicPlaying.value = playlist.getMusic(musicIndex = musicPlayingIndex)
        if (mediaController.currentMediaItemIndex == musicPlayingIndex) {
            mediaController.play()
        } else {
            seekTo(musicIndex = musicPlayingIndex)
        }
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

    fun play() {
        if (!isPlaying.value) {
            this.playPause()
        }
    }

    fun pause() {
        if (isPlaying.value) {
            this.playPause()
        }
    }

    fun getCurrentPosition(): Long {
        return this.mediaController.currentPosition
    }

    fun playNext() {
        if (playlist.musicCount() > 1) {
            this.mediaController.seekToNext()
        }
    }

    fun playPrevious() {
        mediaController.seekToPrevious()
    }

    fun seekTo(positionMs: Long) {
        this.mediaController.seekTo(positionMs)
    }

    fun seekTo(positionPercentage: Float) {
        if (this.musicPlaying.value == null || this.isEnded) {
            throw IllegalStateException("Impossible to seek while no music is playing")
        }

        val maxPosition: Long = this.musicPlaying.value!!.duration
        val newPosition: Long = (positionPercentage * maxPosition).toLong()

        this.seekTo(positionMs = newPosition)
    }

    fun seekTo(music: Music, positionMs: Long = 0) {
        val musicIndex: Int = playlist.getMusicIndex(music)
        seekTo(musicIndex = musicIndex, positionMs)
    }

    fun seekTo(musicId: Long, positionMs: Long = 0) {
        val music: Music = DataManager.getMusic(musicId)
        seekTo(music = music, positionMs)
    }

    fun seekTo(musicIndex: Int, positionMs: Long = 0) {
        mediaController.seekTo(musicIndex, positionMs)
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

    /**
     * Switch the shuffle mode.
     * If there's more than one music in the playlist, then:
     *      1) If the shuffle mode is disabling then undo shuffle.
     *      2) If the shuffle mode is enabling shuffle the playlist
     */
    fun switchShuffleMode() {
        CoroutineScope(Dispatchers.Main).launch {
            isShuffle.value = !isShuffle.value
            if (playlist.musicCount() > 1) {
                if (!isShuffle.value) {
                    // Deactivate shuffle
                    undoShuffle()
                } else {
                    // Activate shuffle mode
                    shuffle()
                }
            }
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
     * Restore the original playlist.
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
        val lastIndex: Int = this.playlist.lastIndex()

        this.musicPlayingIndex = this.playlist.getMusicIndex(this.musicPlaying.value!!)

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