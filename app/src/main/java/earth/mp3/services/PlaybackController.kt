package earth.mp3.services

import android.content.ComponentName
import android.content.Context
import android.os.Environment
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import earth.mp3.models.Music
import java.util.SortedMap

class PlaybackController private constructor(
    context: Context,
    sessionToken: SessionToken,
    musicMap: SortedMap<Long, Music>,
    private val musicMediaItemMap: SortedMap<Music, MediaItem>,
) {
    private lateinit var mediaController: MediaController

    private val originalMusicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicPlayingIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX
    private var isEnded: Boolean = false

    init {
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
        }, ContextCompat.getMainExecutor(context))
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

        // Mutable var are used in ui, it needs to be recomposed
        // I use mutable to avoid using function with multiples params like to add listener
        var musicPlaying: MutableState<Music?> = mutableStateOf(DEFAULT_MUSIC_PLAYING)
        var isPlaying: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_PLAYING_VALUE)
        var repeatMode: MutableState<Int> = mutableIntStateOf(DEFAULT_REPEAT_MODE)
        var isShuffle: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_SHUFFLE)
        var hasNext: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_NEXT)
        var hasPrevious: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_PREVIOUS)

        private lateinit var instance: PlaybackController

        /**
         * Return only one instance of MediaController. If there's no instance already created
         * you have to send a context to the constructor otherwise send null.
         *
         * @param context a context if no instance exists, null otherwise
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
            musicMediaItemMap: SortedMap<Music, MediaItem>,
            musicMap: SortedMap<Long, Music>
        ): PlaybackController {
            if (!Companion::instance.isInitialized) {
                val sessionToken =
                    SessionToken(context, ComponentName(context, PlaybackService::class.java))
                instance = PlaybackController(
                    context = context,
                    sessionToken = sessionToken,
                    musicMap = musicMap,
                    musicMediaItemMap = musicMediaItemMap
                )
            }
            return getInstance()
        }
    }

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_ENDED) {
                isPlaying.value = false
                isEnded = true
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            if (isShuffle.value) {
                // Deactivate shuffle mode
                backToOriginalPlaylist()
            } else {
                // Activate shuffle mode
                shuffle()
            }
            isShuffle.value = shuffleModeEnabled
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            isPlaying.value = playWhenReady
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (mediaItem != musicPlaying.value!!.mediaItem!!) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK
                    || reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
                ) {
                    val previousMusic: Music =
                        if (musicPlayingIndex > 0) musicQueueToPlay[musicPlayingIndex - 1]
                        else musicQueueToPlay.last()

                    if (mediaItem == previousMusic.mediaItem!!) {
                        // Previous button has been clicked
                        when (repeatMode.value) {
                            Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ONE -> {
                                musicPlayingIndex--
                            }

                            Player.REPEAT_MODE_ALL -> {
                                if (previousMusic == musicQueueToPlay.last()) {
                                    musicPlayingIndex = musicQueueToPlay.lastIndex
                                } else {
                                    musicPlayingIndex--
                                }
                            }
                        }
                    } else {
                        // The next button has been clicked
                        when (repeatMode.value) {
                            Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ONE -> {
                                musicPlayingIndex++
                            }

                            Player.REPEAT_MODE_ALL -> {
                                if (musicPlayingIndex + 1 == musicQueueToPlay.size) {
                                    musicPlayingIndex = 0
                                } else {
                                    musicPlayingIndex++
                                }
                            }
                        }
                    }
                }
            }
            musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
            hasNext.value = hasNext()
            hasPrevious.value = hasPrevious()
            mediaController.play()
        }
    }

    /**
     * Start the music in params
     * If music to play is null, play the first music of the playlist otherwise play the musicToPlay
     * If the music to play is already the music playing nothing is done.
     *
     *
     * @param musicToPlay the music to play
     *
     * @throws IllegalStateException if the music list has not been loaded
     */
    @Suppress("NAME_SHADOWING")
    fun start(musicToPlay: Music? = null) {
        if (this.originalMusicQueueToPlay.isEmpty() || musicQueueToPlay.isEmpty()) {
            throw IllegalStateException("The music list to play has not been loaded")
        }
        val musicToPlay: Music? = musicToPlay

        when (musicToPlay) {
            null -> {
                // Play the first music of the list
                musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
                musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
            }

            musicPlaying.value -> {
                //The user wants to play the current music, nothing has to be done
                return
            }

            else -> {
                // the user wants to play a specific music
                for (i: Int in 0..<musicQueueToPlay.size) {
                    val music = musicQueueToPlay[i]
                    if (musicToPlay == music) {
                        musicPlaying.value = musicToPlay
                        musicPlayingIndex = i
                        break
                    }
                }
            }
        }
        mediaController.seekTo(musicPlayingIndex, 0)
        mediaController.play()
        isPlaying.value = true
    }

    /**
     * Play music if it is paused otherwise start music
     */
    fun playPause() {
        if (isPlaying.value) {
            mediaController.pause()
        } else {
            if (isEnded) {
                start()
                isEnded = false
            } else {
                mediaController.play()
            }
        }
    }

    /**
     * Play the next music in queue
     */
    fun next() {
        mediaController.seekToNext()
    }

    /**
     * Get the music from queue, increment the music play index and return the music from the queue
     *
     * @return the first music of the array deque
     * @throws NoSuchElementException if the queue is empty
     */
    private fun getNextMusic(): Music {
        musicPlayingIndex++
        return musicQueueToPlay[musicPlayingIndex]
    }

    fun hasNext(): Boolean {
        return musicPlaying.value != musicQueueToPlay.last()
    }

    /**
     * Add all music from musicMap to the mediaController in the same order.
     * If the shuffle mode is true then shuffle the playlist but the first music of the playlist
     * and the first music to start is musicToPlay.
     *
     * @param musicMap the music map to load if null use the musicQueueToPlay instead
     * @param shuffleMode indicate if the playlist has to be started in shuffle mode by default false
     *
     */
    fun loadMusicToReplace(musicMap: SortedMap<Long, Music>, shuffleMode: Boolean = false) {
        originalMusicQueueToPlay.clear()
        originalMusicQueueToPlay.addAll(musicMap.values)
        musicQueueToPlay = ArrayDeque(musicMap.values)
        if (shuffleMode) {
            shuffle()
            isShuffle.value = true
        }
    }

    /**
     * Add all music from musicMap to the mediaController in the same order.
     * If the shuffle mode is true then shuffle the playlist but the first music of the playlist
     * and the first music to start is musicToPlay.
     *
     * @param musicMap the music map to load if null use the musicQueueToPlay instead
     * @param shuffleMode indicate if the playlist has to be started in shuffle mode by default false
     *
     */
    fun loadMusic(musicMap: SortedMap<Long, Music>? = null, shuffleMode: Boolean = false) {
        val musicListToIterate: List<Music>
        if (musicMap != null) {
            musicListToIterate = musicMap.values.toList()
            resetToDefault()
        } else {
            musicListToIterate = musicQueueToPlay
            mediaController.clearMediaItems()
        }

        for (i: Int in musicListToIterate.indices) {
            val music: Music = musicListToIterate[i]
            if (musicMap != null) {
                if (!originalMusicQueueToPlay.contains(music)) {
                    originalMusicQueueToPlay.add(music)
                    musicQueueToPlay.add(music)
                }
            } else if (music == musicPlaying.value) {
                musicPlayingIndex = i
            }
            mediaController.addMediaItem(music.mediaItem!!)
        }
        if (shuffleMode) {
            shuffle()
            isShuffle.value = true
        }
        mediaController.addListener(listener)
        mediaController.prepare()
    }

    /**
     * Set this class to default values. In other words it clears all
     */
    private fun resetToDefault() {
        mediaController.removeListener(listener)
        originalMusicQueueToPlay.clear()
        musicQueueToPlay.clear()
        mediaController.clearMediaItems()
        musicPlaying.value = DEFAULT_MUSIC_PLAYING
        musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
        isPlaying.value = DEFAULT_IS_PLAYING_VALUE
        repeatMode.value = DEFAULT_REPEAT_MODE
        isShuffle.value = DEFAULT_IS_SHUFFLE
        hasNext.value = DEFAULT_HAS_NEXT
        hasPrevious.value = DEFAULT_HAS_PREVIOUS
    }

    /**
     * Play the previous music in deque
     */
    fun previous() {
        mediaController.seekToPrevious()
    }

    fun hasPrevious(): Boolean {
        return musicPlaying.value != musicQueueToPlay.first()
    }

    /**
     * Switch the repeat mode when repeat mode is:
     *      Disabled: switch to repeat all
     *      Repeat All: switch to repeat one
     *      else: repeat disabled (when it's repeat one for example)
     */
    fun switchRepeatMode() {
        when (repeatMode.value) {
            Player.REPEAT_MODE_OFF -> {
                repeatMode.value = Player.REPEAT_MODE_ALL
            }

            Player.REPEAT_MODE_ALL -> {
                repeatMode.value = Player.REPEAT_MODE_ONE
            }

            Player.REPEAT_MODE_ONE -> {
                repeatMode.value = Player.REPEAT_MODE_OFF
            }
        }
        mediaController.repeatMode = repeatMode.value
    }

    /**
     * Mix the playlist from the playing music if activation
     * Play the original music queue if deactivation
     */
    fun switchShuffleMode() {
        if (isShuffle.value) {
            // Deactivate shuffle mode
            backToOriginalPlaylist()
            isShuffle.value = false
        } else {
            // Activate shuffle mode
            shuffle()
            isShuffle.value = true
        }
    }

    private fun backToOriginalPlaylist() {
        when (musicPlayingIndex) {
            0 -> {
                mediaController.removeMediaItems(1, mediaController.mediaItemCount)
            }

            musicQueueToPlay.size - 1 -> {
                mediaController.removeMediaItems(0, mediaController.mediaItemCount - 1)
            }

            else -> {
                mediaController.removeMediaItems(
                    musicPlayingIndex + 1,
                    mediaController.mediaItemCount
                )
                mediaController.removeMediaItems(0, musicPlayingIndex)
            }
        }
        musicQueueToPlay = ArrayDeque(originalMusicQueueToPlay)

        reloadMusicPlaying()

        for (i: Int in 0..<musicQueueToPlay.size) {
            if (i == musicPlayingIndex) {
                continue
            }
            val mediaItem = musicQueueToPlay[i].mediaItem!!
            if (i < musicPlayingIndex) {
                mediaController.addMediaItem(i, mediaItem)
            } else {
                mediaController.addMediaItem(mediaItem)
            }
        }
    }

    /**
     * It search for the music playing in music queue to play and update musicPlayingIndex
     * and musicPlaying
     */
    private fun reloadMusicPlaying() {
        for (i: Int in 0..<musicQueueToPlay.size) {
            val music = musicQueueToPlay[i]
            if (music == musicPlaying.value) {
                musicPlayingIndex = i
                musicPlaying.value = music
                break
            }
        }
    }


    /**
     * Shuffle music
     */
    private fun shuffle() {
        var startIndex = 1
        if (musicPlaying.value == null) {
            mediaController.clearMediaItems()
            startIndex = DEFAULT_MUSIC_PLAYING_INDEX
        } else {
            mediaController.moveMediaItem(musicPlayingIndex, DEFAULT_MUSIC_PLAYING_INDEX)
            mediaController.removeMediaItems(1, mediaController.mediaItemCount)
            musicQueueToPlay.remove(musicPlaying.value)
        }
        musicQueueToPlay.shuffle()
        if (musicPlaying.value != null) {
            musicQueueToPlay.addFirst(musicPlaying.value!!)
        }
        musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX

        for (i: Int in startIndex..<musicQueueToPlay.size) {
            val music = musicQueueToPlay[i]
            val mediaItem = music.mediaItem
            mediaController.addMediaItem(mediaItem)
            mediaController.prepare()
        }
    }
}