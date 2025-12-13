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

package io.github.antoinepirlot.satunes.playback.services

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.playback.exceptions.AlreadyInPlaybackException
import io.github.antoinepirlot.satunes.playback.models.PlaybackListener
import io.github.antoinepirlot.satunes.playback.models.Playlist

/**
 * @author Antoine Pirlot on 31/01/24
 */

internal class PlaybackController private constructor(
    context: Context,
    sessionToken: SessionToken,
    loadAllMusics: Boolean = false
) {

    internal var mediaController: MediaController? = null

    internal var playlist: Playlist? = null
        private set(value) {
            field = value
            PlaybackManager.playlist = value
        }

    internal var musicPlayingIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX

    var isEnded: Boolean = DEFAULT_IS_ENDED
        internal set(value) {
            field = value
            PlaybackManager.isEnded.value = value
        }

    // Mutable var are used in ui, it needs to be recomposed
    // I use mutable to avoid using function with multiples params like to add listener
    var musicPlaying: Music? = DEFAULT_MUSIC_PLAYING
        internal set(value) {
            field = value
            PlaybackManager.musicPlaying.value = value
            WidgetPlaybackManager.refreshWidgets()
        }
    var isPlaying: Boolean = DEFAULT_IS_PLAYING_VALUE
        internal set(value) {
            field = value
            PlaybackManager.isPlaying.value = value
            WidgetPlaybackManager.refreshWidgets()
        }
    var repeatMode: Int = DEFAULT_REPEAT_MODE
        @OptIn(UnstableApi::class)
        internal set(value) {
            field = value
            PlaybackService.updateCustomCommands()
            PlaybackManager.repeatMode.intValue = value
        }
    var isShuffle: Boolean = DEFAULT_IS_SHUFFLE
        @OptIn(UnstableApi::class)
        internal set(value) {
            field = value
            PlaybackService.updateCustomCommands()
            PlaybackManager.isShuffle.value = value
        }
    var hasNext: Boolean = DEFAULT_HAS_NEXT
        internal set(value) {
            field = value
            PlaybackManager.hasNext.value = value
        }
    var hasPrevious: Boolean = DEFAULT_HAS_PREVIOUS
        internal set(value) {
            field = value
            PlaybackManager.hasPrevious.value = value
        }
    var isLoading: Boolean = DEFAULT_IS_LOADING
        private set(value) {
            field = value
            PlaybackManager.isLoading.value = value
        }
    var isLoaded: Boolean = DEFAULT_IS_LOADED
        private set(value) {
            field = value
            PlaybackManager.isLoaded.value = value
            WidgetPlaybackManager.refreshWidgets()
        }
    var currentPositionProgression: Float = DEFAULT_CURRENT_POSITION_PROGRESSION
        internal set(value) {
            field = value
            PlaybackManager.currentPositionProgression.floatValue = value
        }

    private var listener: Player.Listener = PlaybackListener()

    companion object {

        internal const val DEFAULT_MUSIC_PLAYING_INDEX: Int = 0
        internal const val DEFAULT_IS_ENDED: Boolean = false
        internal const val DEFAULT_IS_PLAYING_VALUE: Boolean = false
        internal val DEFAULT_REPEAT_MODE: Int = SettingsManager.repeatMode
        internal val DEFAULT_IS_SHUFFLE: Boolean = SettingsManager.shuffleMode
        internal const val DEFAULT_HAS_NEXT: Boolean = false
        internal const val DEFAULT_HAS_PREVIOUS: Boolean = false
        internal const val DEFAULT_IS_LOADING: Boolean = false
        internal const val DEFAULT_IS_LOADED: Boolean = false
        internal const val DEFAULT_CURRENT_POSITION_PROGRESSION: Float = 0f
        internal val DEFAULT_MUSIC_PLAYING = null

        private var _instance: PlaybackController? = null
        private val _logger: Logger? = Logger.getLogger()
        private val _forwardMs: MutableLongState = SettingsManager.forwardMs
        private val _rewindMs: MutableLongState = SettingsManager.rewindMs

        /**
         * Return only one instance of MediaController. If there's no instance already created
         * you have to send a context to the constructor otherwise send null.
         *
         * @return the instance of MediaController
         */
        fun getInstance(): PlaybackController {
            _logger?.info("Get instance")
            // TODO issues relaunch app happens here
            if (_instance == null) {
                //TODO find a way to fix crashing app after resume after inactivity
                val message = "The PlayBackController has not been initialized"
                _logger?.severe(message)
                throw IllegalStateException(message)
            }
            return _instance!!
        }

        @OptIn(UnstableApi::class)
        fun initInstance(
            context: Context,
            listener: Player.Listener? = null,
            loadAllMusics: Boolean = false
        ): PlaybackController {
            _logger?.info("Init instance")
            val isInitializing: Boolean = _instance == null
            if (isInitializing) {
                val sessionToken =
                    SessionToken(
                        context.applicationContext,
                        ComponentName(context, PlaybackService::class.java)
                    )

                _instance = PlaybackController(
                    context = context.applicationContext,
                    sessionToken = sessionToken,
                    loadAllMusics = loadAllMusics,
                )
            }
            updateListener(isInitializing = isInitializing, listener = listener)
            return getInstance()
        }

        fun updateListener(listener: Player.Listener?) =
            this.updateListener(isInitializing = false, listener = listener)

        private fun updateListener(isInitializing: Boolean, listener: Player.Listener?) {
            if (!isInitializing) {
                if (listener != null && listener != this._instance?.listener) {
                    _logger?.info("Update listener")

                    while (!isInitialized()) {
                        _logger?.info("Waiting")
                        // Wait it is initializing
                    }
                    val wasPlaying: Boolean = _instance!!.isPlaying
                    if (_instance!!.isPlaying) {
                        _instance!!.pause()
                    }
                    _instance!!.mediaController!!.removeListener(_instance!!.listener)
                    _instance!!.mediaController!!.addListener(listener)
                    _instance!!.mediaController!!.prepare()
                    if (wasPlaying) {
                        _instance!!.play()
                    }
                }
            }

            if (listener != null && listener != this._instance?.listener) {
                _instance!!.listener = listener
                _logger?.info("Listener loaded or changed")
            }
        }

        internal fun isInitialized(): Boolean =
            this._instance != null && this._instance!!.mediaController != null
    }

    init {
        _logger?.info("Init class")

        if (loadAllMusics) {
            isLoading = true
        }

        try {
            val mediaControllerFuture: ListenableFuture<MediaController> =
                MediaController.Builder(context, sessionToken).buildAsync()

            mediaControllerFuture.addListener(
                {
                    mediaController = mediaControllerFuture.get()
                    if (loadAllMusics) {
                        this.loadMusics(musics = DataManager.getMusicSet())
                    }
                    PlaybackManager.isInitialized.value = true
                },
                MoreExecutors.directExecutor()
            )
        } catch (e: Throwable) {
            _logger?.severe(e.message)
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
        if (!isLoaded) {
            throw IllegalStateException("The playlist has not been loaded, you can't play music")
        }
        when (musicToPlay) {
            null -> {
                //Keep it first to prevent the first playing is always null and so... app crash
                //Play from the beginning
                musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
            }

            this.musicPlaying -> {
                // The music playing is the same as the music to play, nothing is done
                return
            }

            else -> {
                // The music to play has to be played
                musicPlayingIndex = playlist!!.getMusicIndex(music = musicToPlay)
            }
        }
        if (musicPlayingIndex == -1) {
            val musicSet: Set<Music> =
                if (!DataManager.getMusicSet().contains(element = musicToPlay)) {
                    val musicSet: MutableSet<Music> = mutableSetOf()
                    musicSet.addAll(elements = DataManager.getMusicSet())
                    musicSet.add(element = musicToPlay!!)
                    musicSet
                } else DataManager.getMusicSet()
            this.loadMusics(musics = musicSet, musicToPlay = musicToPlay)
            this.start(musicToPlay = musicToPlay)
        } else {
            musicPlaying = playlist!!.getMusic(musicIndex = musicPlayingIndex)

            if (mediaController!!.currentMediaItemIndex == musicPlayingIndex)
                mediaController!!.play()
            else
                mediaController!!.seekTo(musicPlayingIndex, 0)
        }
    }

    fun playPause() {
        if (this.isPlaying) {
            this.mediaController!!.pause()
        } else {
            if (this.isEnded) {
                this.start()
                this.isEnded = false
            } else {
                this.mediaController!!.play()
            }
        }
    }

    fun play() {
        if (!isPlaying) {
            this.playPause()
        }
    }

    fun pause() {
        if (isPlaying) {
            this.playPause()
        }
    }

    fun getCurrentPosition(): Long {
        return this.mediaController!!.currentPosition
    }

    fun getMusicPlayingIndexPosition(): Int {
        return this.mediaController!!.currentMediaItemIndex
    }

    fun playNext() {
        if (playlist!!.musicCount() > 1) {
            this.mediaController!!.seekToNext()
        }
    }

    fun playPrevious() {
        mediaController!!.seekToPrevious()
    }

    fun seekTo(positionMs: Long) {
        this.mediaController!!.seekTo(positionMs)
    }

    fun seekTo(positionPercentage: Float) {
        //position changed on listener events
        if (this.musicPlaying == null || this.isEnded) {
            val message = """"
                |Impossible to seek while no music is playing 
                |$this"""".trimMargin()
            _logger?.severe(message)
            throw IllegalStateException(message)
        }

        val maxPosition: Long = this.musicPlaying!!.durationMs
        val newPosition: Long = (positionPercentage * maxPosition).toLong()

        this.seekTo(positionMs = newPosition)
    }

    fun seekTo(music: Music, positionMs: Long = 0) {
        val musicIndex: Int = playlist!!.getMusicIndex(music)
        mediaController!!.seekTo(musicIndex, positionMs)
    }

    /**
     * Add all music from musicMap to the mediaController in the same order.
     * If the shuffle mode is true then shuffle the playlist
     *
     * @param musics the [Music] [Collection] to load
     * @param shuffleMode indicate if the playlistDB has to be started in shuffle mode by default false
     * @param musicToPlay the music to play
     *
     */
    fun loadMusics(
        musics: Collection<Music>,
        shuffleMode: Boolean = SettingsManager.shuffleMode,
        musicToPlay: Music? = null,
    ) {
        this.isLoading = true
        val playlist = Playlist(musics = musics)
        if (shuffleMode) {
            if (musicToPlay == null) {
                playlist.shuffle()
            } else {
                playlist.shuffle(musicIndex = playlist.getMusicIndex(music = musicToPlay))
            }
        }
        this.loadMusics(playlist = playlist)
    }

    @OptIn(UnstableApi::class)
    fun loadMusics(playlist: Playlist) {
        this.isLoading = true
        this.playlist = playlist

        this.mediaController!!.clearMediaItems()
        this.mediaController!!.addMediaItems(this.playlist!!.mediaItemList)
        this.mediaController!!.removeListener(listener)
        this.mediaController!!.addListener(listener)
        this.mediaController!!.repeatMode = when (SettingsManager.repeatMode) {
            1 -> Player.REPEAT_MODE_ALL
            2 -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF // For 0 and other incorrect numbers
        }
        this.mediaController!!.prepare()

        this.isShuffle = this.playlist!!.isShuffle
        this.repeatMode = this.mediaController!!.repeatMode
        PlaybackService.updateCustomCommands()
        this.isLoaded = true
        this.isLoading = false
    }

    fun addToQueue(mediaImplList: Collection<MediaImpl>) {
        val listToProcess: Collection<MediaImpl> =
            if (SettingsManager.shuffleMode) mediaImplList.shuffled()
            else mediaImplList

        listToProcess.forEach { mediaImpl: MediaImpl ->
            addToQueue(mediaImpl = mediaImpl)
        }
    }

    fun addToQueue(mediaImpl: MediaImpl) {
        if (mediaImpl.isMusic()) {
            mediaImpl as Music
            try {
                this.playlist!!.addToQueue(music = mediaImpl)
                this.mediaController!!.addMediaItem(mediaImpl.mediaItem)
            } catch (_: AlreadyInPlaybackException) {
                return
            }
            hasNext = true
        } else if (mediaImpl.isFolder())
            addToQueue(mediaImplList = (mediaImpl as Folder).getAllMusic())
        else addToQueue(mediaImplList = mediaImpl.musicCollection)
    }

    fun removeFromQueue(mediaImplList: Collection<MediaImpl>) {
        mediaImplList.forEach { mediaImpl: MediaImpl ->
            removeFromQueue(mediaImpl = mediaImpl)
        }
    }

    fun removeFromQueue(mediaImpl: MediaImpl) {
        if (mediaImpl == musicPlaying) return

        if (mediaImpl.isMusic()) {
            mediaImpl as Music
            val musicIndex: Int = this.playlist!!.removeFromQueue(music = mediaImpl)
            if (musicIndex >= 0) {
                this.mediaController!!.removeMediaItem(musicIndex)
                updateHasNext()
                updateHasPrevious()
            }
        } else if (mediaImpl.isFolder())
            removeFromQueue(mediaImplList = (mediaImpl as Folder).getAllMusic())
        else removeFromQueue(mediaImplList = mediaImpl.musicCollection)
    }

    private fun updateHasNext() {
        if (this.mediaController!!.currentMediaItemIndex == this.mediaController!!.mediaItemCount - 1) {
            hasNext = false
        }
    }

    private fun updateHasPrevious() {
        if (this.mediaController!!.currentMediaItemIndex == 0) {
            hasPrevious = false
        }
    }

    private fun addNext(mediaImplList: Collection<MediaImpl>) {
        val listToProcess: Collection<MediaImpl> =
            if (SettingsManager.shuffleMode) mediaImplList.shuffled()
            else mediaImplList

        listToProcess.forEach { mediaImpl: MediaImpl ->
            addNext(mediaImpl = mediaImpl)
        }
    }

    fun addNext(mediaImpl: MediaImpl) {
        if (musicPlaying == mediaImpl) return
        if (mediaImpl.isMusic()) {
            mediaImpl as Music
            try {
                this.playlist!!.addNext(index = this.musicPlayingIndex + 1, music = mediaImpl)
                this.mediaController!!.addMediaItem(
                    this.musicPlayingIndex + 1,
                    mediaImpl.mediaItem
                )
                hasNext = true
            } catch (_: AlreadyInPlaybackException) {
                this.moveMusic(music = mediaImpl, newIndex = this.musicPlayingIndex + 1)
                hasNext = true
            }
        } else if (mediaImpl.isFolder())
            // reversed because each music will be added next to it's added after the current music
            addNext(mediaImplList = (mediaImpl as Folder).getAllMusic().reversed())
        // reversed because each music will be added next to it's added after the current music
        else addNext(mediaImplList = mediaImpl.musicCollection.reversed())
    }

    private fun moveMusic(music: Music, newIndex: Int) {
        val musicToMoveIndex: Int = this.playlist!!.getMusicIndex(music = music)
        if (musicToMoveIndex == -1) {
            throw IllegalArgumentException("This music is not inside the playlist")
        }

        if (musicToMoveIndex < this.musicPlayingIndex) {
            this.playlist!!.moveMusic(
                music = music,
                oldIndex = musicToMoveIndex,
                newIndex = newIndex - 1
            )
            this.mediaController!!.moveMediaItem(musicToMoveIndex, newIndex - 1)
            this.musicPlayingIndex = this.mediaController!!.currentMediaItemIndex
        } else {
            this.playlist!!.moveMusic(
                music = music,
                oldIndex = musicToMoveIndex,
                newIndex = newIndex
            )
            this.mediaController!!.moveMediaItem(musicToMoveIndex, newIndex)
        }
    }

    /**
     * Switch the shuffle mode.
     * If there's more than one music in the playlistDB, then:
     *      1) If the shuffle mode is disabling then undo shuffle.
     *      2) If the shuffle mode is enabling shuffle the playlistDB
     */
    @OptIn(UnstableApi::class)
    fun switchShuffleMode() {
        isShuffle = !isShuffle
        if (playlist!!.musicCount() > 1) {
            if (!isShuffle) {
                // Deactivate shuffle
                undoShuffle()
            } else {
                // Activate shuffle mode
                shuffle()
            }
        }
    }

    /**
     * Move music playing to the first index and remove other
     * the music playing has to take its original place.
     */
    @OptIn(UnstableApi::class)
    private fun shuffle() {
        if (this.musicPlaying == null) {
            // No music playing
            this.playlist!!.shuffle()
            return
        } else {
            this.playlist!!.shuffle(musicIndex = this.musicPlayingIndex)
        }

        //A music is playing
        this.mediaController!!.moveMediaItem(
            this.mediaController!!.currentMediaItemIndex,
            DEFAULT_MUSIC_PLAYING_INDEX
        )

        val fromIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX + 1
        val toIndex: Int = this.playlist!!.lastIndex()

        this.mediaController!!.replaceMediaItems(
            fromIndex,
            toIndex + 1, // +1 as it is a toIndex excluded
            this.playlist!!.getMediaItems(fromIndex = fromIndex, toIndex = toIndex)
        )

        this.musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
    }

    /**
     * Restore the original playlistDB.
     *
     */
    @OptIn(UnstableApi::class)
    private fun undoShuffle() {
        this.playlist!!.undoShuffle()
        if (this.musicPlaying == null) {
            // No music playing
            this.mediaController!!.clearMediaItems()
            this.mediaController!!.addMediaItems(this.playlist!!.mediaItemList)

            return
        }

        val oldMusicPlayingIndex = this.musicPlayingIndex
        val lastIndex: Int = this.playlist!!.lastIndex()

        this.musicPlayingIndex = this.playlist!!.getMusicIndex(this.musicPlaying!!)

        when (this.musicPlayingIndex) {
            DEFAULT_MUSIC_PLAYING_INDEX -> {
                // Music playing is at the index 0, replace index 1 to last index
                // The original place is the first
                this.mediaController!!.moveMediaItem(
                    oldMusicPlayingIndex,
                    DEFAULT_MUSIC_PLAYING_INDEX
                )

                val fromIndex: Int = this.musicPlayingIndex + 1

                this.mediaController!!.replaceMediaItems(
                    fromIndex,
                    lastIndex + 1,
                    this.playlist!!.getMediaItems(fromIndex = fromIndex, toIndex = lastIndex)
                )
            }

            lastIndex -> {
                // The music is at the last index, replace from index 0 to last index -1
                this.mediaController!!.moveMediaItem(
                    oldMusicPlayingIndex,
                    this.musicPlayingIndex + 1
                )

                this.mediaController!!.replaceMediaItems(
                    DEFAULT_MUSIC_PLAYING_INDEX,
                    lastIndex,
                    this.playlist!!.getMediaItems(
                        fromIndex = DEFAULT_MUSIC_PLAYING_INDEX,
                        toIndex = lastIndex - 1
                    )
                )
            }

            else -> {
                // Music playing is not at the easy position, replace before music playing and after
                this.mediaController!!.moveMediaItem(
                    oldMusicPlayingIndex,
                    this.musicPlayingIndex
                )

                this.mediaController!!.replaceMediaItems(
                    0,
                    this.musicPlayingIndex,
                    this.playlist!!.getMediaItems(
                        fromIndex = 0,
                        toIndex = this.musicPlayingIndex - 1
                    )
                )

                this.mediaController!!.replaceMediaItems(
                    this.musicPlayingIndex + 1,
                    lastIndex + 1,
                    this.playlist!!.getMediaItems(
                        fromIndex = this.musicPlayingIndex + 1,
                        toIndex = lastIndex
                    )
                )
            }
        }
    }

    fun switchRepeatMode() {
        when (this.repeatMode) {
            Player.REPEAT_MODE_OFF -> {
                this.repeatMode = Player.REPEAT_MODE_ALL
            }

            Player.REPEAT_MODE_ALL -> {
                this.repeatMode = Player.REPEAT_MODE_ONE
            }

            Player.REPEAT_MODE_ONE -> {
                this.repeatMode = Player.REPEAT_MODE_OFF
            }
        }

        this.mediaController!!.repeatMode = this.repeatMode
    }

    fun stop() {
        this.mediaController?.stop()
        this.mediaController?.clearMediaItems()
            this.musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
            this.isPlaying = DEFAULT_IS_PLAYING_VALUE
            this.isShuffle = DEFAULT_IS_SHUFFLE
            this.hasNext = DEFAULT_HAS_NEXT
            this.hasPrevious = DEFAULT_HAS_PREVIOUS
            this.isLoaded = DEFAULT_IS_LOADED
            this.currentPositionProgression = DEFAULT_CURRENT_POSITION_PROGRESSION
            this.playlist = null
            this.repeatMode = DEFAULT_REPEAT_MODE
            this.musicPlaying = DEFAULT_MUSIC_PLAYING
    }

    fun release() {
        _logger?.info("Releasing $this")
        PlaybackManager.isInitialized.value = false
        if (_instance != null) {
            this.stop()
            this.mediaController?.release()
            _instance = null
        }
        _logger?.info("PlaybackController released")
    }

    fun getPlaylist(): SnapshotStateList<Music> {
        return this.playlist!!.musicList
    }

    fun isMusicInQueue(music: Music): Boolean {
        return this.playlist!!.isMusicInQueue(music = music)
    }

    override fun toString(): String {
        val mediaControllerInit: Boolean = this.mediaController != null
        return """
            PlaybackController:
            musicPlayingIndex: $musicPlayingIndex
            mediaController initialized: $mediaControllerInit
            musicPlayingIndex in MediaController: ${if (mediaControllerInit) mediaController!!.currentMediaItemIndex else "/"}
            musicPlaying != null: ${musicPlaying != null}
            isPlaying: $isPlaying
            repeatMode: $repeatMode
            isShuffle: $isShuffle
            hasNext: $hasNext
            hasPrevious: $hasPrevious
            isLoaded: $isLoaded
            currentPositionProgression: $currentPositionProgression
            isEnded: $isEnded
        """.trimIndent()
    }

    fun updateCurrentPosition() {
        if (musicPlaying == null) return
        val maxPosition: Long = this.musicPlaying!!.durationMs
        val newPosition: Long = this.getCurrentPosition()
        this.currentPositionProgression =
            newPosition.toFloat() / maxPosition.toFloat()
    }

    fun getNextMusic(): Music? {
        return if (this.musicPlayingIndex == this.playlist!!.lastIndex()) {
            null
        } else {
            this.playlist!!.getMusic(musicIndex = this.musicPlayingIndex + 1)
        }
    }

    fun forward() = movePoisitionOf(microSeconds = _forwardMs.longValue)
    fun rewind() = movePoisitionOf(microSeconds = -_rewindMs.longValue)

    private fun movePoisitionOf(microSeconds: Long) {
        if (this.musicPlaying == null) return
        val newPosition = this.getCurrentPosition() + microSeconds
        if (newPosition > this.musicPlaying!!.durationMs) this.playNext()
        else if (newPosition < 0) this.seekTo(positionMs = 0)
        else this.seekTo(positionMs = newPosition)
    }
}