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

package io.github.antoinepirlot.satunes.playback.services

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.playback.exceptions.AlreadyInPlaybackException
import io.github.antoinepirlot.satunes.playback.models.PlaybackListener
import io.github.antoinepirlot.satunes.playback.models.Playlist
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 31/01/24
 */

class PlaybackController private constructor(
    context: Context,
    sessionToken: SessionToken,
) {
    internal lateinit var mediaController: MediaController

    internal lateinit var playlist: Playlist

    internal var musicPlayingIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX
    var isEnded: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_ENDED)

    // Mutable var are used in ui, it needs to be recomposed
    // I use mutable to avoid using function with multiples params like to add listener
    val musicPlaying: MutableState<Music?> = mutableStateOf(DEFAULT_MUSIC_PLAYING)
    val isPlaying: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_PLAYING_VALUE)
    val repeatMode: MutableIntState = mutableIntStateOf(DEFAULT_REPEAT_MODE)
    val isShuffle: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_SHUFFLE)
    val hasNext: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_NEXT)
    val hasPrevious: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_PREVIOUS)
    val isLoaded: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_LOADED)
    val currentPositionProgression: MutableFloatState =
        mutableFloatStateOf(DEFAULT_CURRENT_POSITION_PROGRESSION)

    private var listener: Player.Listener = PlaybackListener()

    companion object {
        internal const val DEFAULT_MUSIC_PLAYING_INDEX: Int = 0
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
        private val logger = SatunesLogger.getLogger()

        /**
         * Return only one instance of MediaController. If there's no instance already created
         * you have to send a context to the constructor otherwise send null.
         *
         * @return the instance of MediaController
         */
        fun getInstance(): PlaybackController {
            // TODO issues relaunch app happens here
            if (!Companion::instance.isInitialized) {
                //TODO find a way to fix crashing app after resume after inactivity
                val message = "The PlayBackController has not been initialized"
                logger.severe(message)
                throw IllegalStateException(message)
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

            return getInstance()
        }
    }

    init {
        try {
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

            controllerFuture.addListener({
                this.mediaController = controllerFuture.get()
            }, ContextCompat.getMainExecutor(context))
        } catch (e: Throwable) {
            logger.severe(e.message)
            throw e
        }
    }

    /**
     * Start the playback.
     *
     * If the music to play is null, then play the first music of the playlistDB, otherwise play the
     * music to play.
     *
     * If music to play is the music playing, then do nothing.
     *
     * @param musicToPlay the music to play if it's not null, by default it's null.
     */
    fun start(musicToPlay: Music? = null) {
        if (!isLoaded.value) {
            throw IllegalStateException("The playlistDB has not been loaded, you can't play music")
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
            if (this.isEnded.value) {
                this.start()
                this.isEnded.value = false
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

    fun getMusicPlayingIndexPosition(): Int {
        return this.mediaController.currentMediaItemIndex
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
        //position changed on listener events
        if (this.musicPlaying.value == null || this.isEnded.value) {
            val message = """"
                |Impossible to seek while no music is playing 
                |$this"""".trimMargin()
            logger.severe(message)
            throw IllegalStateException(message)
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
     * @param musicSet the music Set to load
     * @param shuffleMode indicate if the playlistDB has to be started in shuffle mode by default false
     * @param musicToPlay the music to play
     *
     */
    fun loadMusic(
        musicSet: Set<Music>,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null,
    ) {
        this.playlist = Playlist(musicSet = musicSet)
        if (shuffleMode) {
            if (musicToPlay == null) {
                this.playlist.shuffle()
            } else {
                this.playlist.shuffle(musicIndex = this.playlist.getMusicIndex(music = musicToPlay))
            }
        }
        this.mediaController.clearMediaItems()
        this.mediaController.addMediaItems(this.playlist.mediaItemList)
        this.mediaController.removeListener(listener)
        this.mediaController.addListener(listener)
        this.mediaController.repeatMode = when (SettingsManager.repeatMode) {
            1 -> Player.REPEAT_MODE_ALL
            2 -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF // For 0 and other incorrect numbers
        }
        this.mediaController.prepare()

        this.isLoaded.value = true
        this.isShuffle.value = shuffleMode
    }

    fun addToQueue(mediaImplList: Collection<MediaImpl>) {
        CoroutineScope(Dispatchers.Main).launch {
            mediaImplList.forEach { mediaImpl: MediaImpl ->
                addToQueue(mediaImpl = mediaImpl)
            }
        }
    }

    fun addToQueue(mediaImpl: MediaImpl) {
        when (mediaImpl) {
            is Music -> {
                try {
                    this.playlist.addToQueue(music = mediaImpl)
                    this.mediaController.addMediaItem(mediaImpl.mediaItem)
                } catch (e: AlreadyInPlaybackException) {
                    return
                }
                hasNext.value = true
            }

            is Folder -> addToQueue(mediaImplList = mediaImpl.getAllMusic().reversed())

            else -> {
                addToQueue(mediaImplList = mediaImpl.getMusicSet().reversed())
            }
        }
    }

    private fun addNext(mediaImplList: Collection<MediaImpl>) {
        CoroutineScope(Dispatchers.Main).launch {
            mediaImplList.forEach { mediaImpl: MediaImpl ->
                addNext(mediaImpl = mediaImpl)
            }
        }
    }

    fun addNext(mediaImpl: MediaImpl) {
        if (musicPlaying.value == mediaImpl) {
            return
        }
        when (mediaImpl) {
            is Music -> {
                try {
                    this.playlist.addNext(index = this.musicPlayingIndex + 1, music = mediaImpl)
                    this.mediaController.addMediaItem(
                        this.musicPlayingIndex + 1,
                        mediaImpl.mediaItem
                    )
                } catch (e: AlreadyInPlaybackException) {
                    this.moveMusic(music = mediaImpl, newIndex = this.musicPlayingIndex + 1)
                }
                hasNext.value = true
            }

            is Folder -> addNext(mediaImplList = mediaImpl.getAllMusic().reversed())

            else -> {
                addNext(mediaImplList = mediaImpl.getMusicSet().reversed())
            }
        }
    }

    private fun moveMusic(music: Music, newIndex: Int) {
        val musicToMoveIndex: Int = this.playlist.getMusicIndex(music = music)
        if (musicToMoveIndex == -1) {
            throw IllegalArgumentException("This music is not inside the playlist")
        }

        if (musicToMoveIndex < this.musicPlayingIndex) {
            this.playlist.moveMusic(
                music = music,
                oldIndex = musicToMoveIndex,
                newIndex = newIndex - 1
            )
            this.mediaController.moveMediaItem(musicToMoveIndex, newIndex)
            this.musicPlayingIndex -= 1
        } else {
            this.playlist.moveMusic(music = music, oldIndex = musicToMoveIndex, newIndex = newIndex)
            this.mediaController.moveMediaItem(musicToMoveIndex, newIndex)
        }
    }

    /**
     * Switch the shuffle mode.
     * If there's more than one music in the playlistDB, then:
     *      1) If the shuffle mode is disabling then undo shuffle.
     *      2) If the shuffle mode is enabling shuffle the playlistDB
     */
    fun switchShuffleMode() {
        isShuffle.value = !isShuffle.value
        CoroutineScope(Dispatchers.Main).launch {
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
        val toIndex: Int = this.playlist.lastIndex()

        this.mediaController.replaceMediaItems(
            fromIndex,
            toIndex + 1, // +1 as it is a toIndex excluded
            this.playlist.getMediaItems(fromIndex = fromIndex, toIndex = toIndex)
        )

        this.musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
    }

    /**
     * Restore the original playlistDB.
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
        when (this.repeatMode.intValue) {
            Player.REPEAT_MODE_OFF -> {
                this.repeatMode.intValue = Player.REPEAT_MODE_ALL
            }

            Player.REPEAT_MODE_ALL -> {
                this.repeatMode.intValue = Player.REPEAT_MODE_ONE
            }

            Player.REPEAT_MODE_ONE -> {
                this.repeatMode.intValue = Player.REPEAT_MODE_OFF
            }
        }

        this.mediaController.repeatMode = this.repeatMode.intValue
    }

    fun stop() {
        if (this::mediaController.isInitialized) {
            this.mediaController.stop()
        }
    }

    fun release() {
        logger.info("Releasing $this")
        this.stop()
        if (this::mediaController.isInitialized) {
            this.mediaController.release()
        }
        logger.info("PlaybackController released")
    }

    fun getPlaylist(): SnapshotStateList<Music> {
        return this.playlist.musicList
    }

    fun isMusicInQueue(music: Music): Boolean {
        return this.playlist.isMusicInQueue(music = music)
    }

    override fun toString(): String {
        val mediaControllerInit: Boolean = this::mediaController.isInitialized
        return """
            PlaybackController:
            musicPlayingIndex: $musicPlayingIndex
            mediaController initialized: $mediaControllerInit
            musicPlayingIndex in MediaController: ${if (mediaControllerInit) mediaController.currentMediaItemIndex else "/"}
            musicPlaying != null: ${musicPlaying.value != null}
            isPlaying: ${isPlaying.value}
            repeatMode: ${repeatMode.intValue}
            isShuffle: ${isShuffle.value}
            hasNext: ${hasNext.value}
            hasPrevious: ${hasPrevious.value}
            isLoaded: ${isLoaded.value}
            currentPositionProgression: ${currentPositionProgression.floatValue}
            isEnded: $isEnded
        """.trimIndent()
    }

    fun updateCurrentPosition() {
        val maxPosition: Long = this.musicPlaying.value!!.duration
        val newPosition: Long = this.getCurrentPosition()
        this.currentPositionProgression.floatValue =
            newPosition.toFloat() / maxPosition.toFloat()
    }
}