package earth.mp3.services

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
import earth.mp3.models.Music
import earth.mp3.models.Playlist
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
    private var isEnded: Boolean = false

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
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            currentPositionProgression.floatValue = newPosition.positionMs.toFloat()
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
                        // Previous button has been clicked
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
        val ROOT_PATH: String = Environment.getExternalStorageDirectory().path
        val DEFAULT_MUSIC_PLAYING = null
        const val DEFAULT_MUSIC_PLAYING_INDEX = 0
        const val DEFAULT_IS_PLAYING_VALUE = false
        const val DEFAULT_REPEAT_MODE = Player.REPEAT_MODE_OFF
        const val DEFAULT_IS_SHUFFLE = false
        const val DEFAULT_HAS_NEXT = false
        const val DEFAULT_HAS_PREVIOUS = false
        const val DEFAULT_IS_LOADED = false
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
        this.isPlaying.value = true
        this.isEnded = false
        //Starting updating progression
        updateCurrentPosition()
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
                //Starting updating progression
                updateCurrentPosition()
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

    private fun updateCurrentPosition() {
        if (musicPlaying.value == null) {
            return
        }
        val maxPosition: Long = this.musicPlaying.value!!.duration
        CoroutineScope(Dispatchers.Main).launch {
            while (isPlaying.value) {
                val newPosition: Long = mediaController.currentPosition
                currentPositionProgression.floatValue =
                    newPosition.toFloat() / maxPosition.toFloat()
                delay(1000) // Wait one second to avoid refreshing all the time
            }
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
        mediaController.addListener(listener)
        mediaController.prepare()

        isLoaded.value = true
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