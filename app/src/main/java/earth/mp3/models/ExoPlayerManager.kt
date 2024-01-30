package earth.mp3.models

import android.content.Context
import android.os.Environment
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.TrackSelectionParameters.AudioOffloadPreferences
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerManager @OptIn(UnstableApi::class) private constructor(context: Context) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private val originalMusicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicPlayingIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX
    private var isEnded: Boolean = false

    // Mutable var are used in ui, it needs to be recomposed
    // I use mutable to avoid using function with multiples params like to add listener
    var musicPlaying: MutableState<Music?> = mutableStateOf(null)
    var isPlaying: MutableState<Boolean> = mutableStateOf(false)
    var repeatMode: MutableState<Int> = mutableIntStateOf(REPEAT_MODE_OFF)
    var shuffleMode: MutableState<Boolean> = mutableStateOf(false)
    var hasNext: MutableState<Boolean> = mutableStateOf(false)
    var hasPrevious: MutableState<Boolean> = mutableStateOf(false)

    init {
        val audioOffloadPreferences = AudioOffloadPreferences.Builder()
            .setAudioOffloadMode(AudioOffloadPreferences.AUDIO_OFFLOAD_MODE_ENABLED)
            .setIsGaplessSupportRequired(true)
            .build()

        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
            .buildUpon()
            .setAudioOffloadPreferences(audioOffloadPreferences)
            .build()
    }

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == STATE_ENDED) {
                isPlaying.value = false
                isEnded = true
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                when (repeatMode.value) {
                    REPEAT_MODE_OFF -> {
                        musicPlayingIndex++
                        musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
                        hasNext.value = hasNext()
                        hasPrevious.value = hasPrevious()
                    }

                    REPEAT_MODE_ALL -> {
                        if (musicPlayingIndex + 1 == musicQueueToPlay.size) {
                            musicPlayingIndex = 0
                        } else {
                            musicPlayingIndex++
                        }

                    }
                }
                musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
                hasNext.value = hasNext()
                hasPrevious.value = hasPrevious()
            }
        }
    }

    companion object {
        val ROOT_PATH: String = Environment.getExternalStorageDirectory().path
        const val DEFAULT_MUSIC_PLAYING_INDEX = -1

        private lateinit var instance: ExoPlayerManager

        /**
         * Return only one instance of ExoPlayerManager. If there's no instance already created
         * you have to send a context to the constructor otherwise send null.
         *
         * @param context a context if no instance exists, null otherwise
         *
         * @return the instance of ExoPlayerManager
         */
        fun getInstance(context: Context?): ExoPlayerManager {
            if (context == null && !::instance.isInitialized) {
                throw IllegalStateException("The ExoPlayerManager is not instanced, it needs a context")
            }
            if (!::instance.isInitialized) {
                instance = ExoPlayerManager(context!!)
            }
            return instance
        }
    }

    /**
     * Start the music in params
     * If music to play is null, play the first music of the playlist otherwise play the musicToPlay
     * If the music to play is already the music playing nothing is done.
     *
     * @param musicToPlay the music to play
     */
    fun start(musicToPlay: Music? = null) {
        if (musicToPlay != null) {
            if (musicToPlay == musicPlaying.value) {
                return
            }
            for (i: Int in 0..<musicQueueToPlay.size) {
                val music = musicQueueToPlay[i]
                if (musicToPlay == music) {
                    musicPlaying.value = musicToPlay
                    musicPlayingIndex = i
                    break
                }
            }
        } else {
            musicPlayingIndex = 0
            musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
        }
        exoPlayer.seekTo(musicPlayingIndex, 0)
        hasNext.value = hasNext()
        hasPrevious.value = hasPrevious()
        exoPlayer.prepare()
        exoPlayer.play()
        switchIsPlaying()
    }

    /**
     * Play music if it is paused otherwise start music
     */
    fun playPause() {
        if (isPlaying.value) {
            exoPlayer.pause()
            switchIsPlaying()
        } else {
            if (isEnded) {
                start()
                isEnded = false
            } else {
                exoPlayer.play()
                switchIsPlaying()
            }
        }
    }

    private fun switchIsPlaying() {
        isPlaying.value = !isPlaying.value
    }

    /**
     * Play the next music in queue
     */
    fun next() {
        if (hasNext()) {
            exoPlayer.seekToNext()
            musicPlaying.value = getNextMusic()
            hasNext.value = hasNext()
            hasPrevious.value = hasPrevious()
            exoPlayer.prepare()
            exoPlayer.play()
            isPlaying.value = true
        }
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
     * Add all music from musicMap to the exoPlayer in the same order
     */
    fun loadMusic(musicList: List<Music>? = null) {
        if (musicList != null) {
            resetToDefault()
            for (music in musicList) {
                if (!originalMusicQueueToPlay.contains(music)) {
                    originalMusicQueueToPlay.add(music)
                    musicQueueToPlay.add(music)
                    val mediaItem = MediaItem.fromUri(music.getAbsolutePath())
                    exoPlayer.addMediaItem(mediaItem)
                }
            }
            exoPlayer.addListener(listener)
        } else {
            exoPlayer.clearMediaItems()
            for (music in musicQueueToPlay) {
                val mediaItem = MediaItem.fromUri(music.getAbsolutePath())
                exoPlayer.addMediaItem(mediaItem)
            }
        }
    }

    /**
     * Set the exo player to the default state without media items. clear all
     */
    private fun resetToDefault() {
        exoPlayer.removeListener(listener)
        musicQueueToPlay.clear()
        exoPlayer.clearMediaItems()
        musicPlaying.value = null
        musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
        isPlaying.value = false
    }

    /**
     * Play the previous music in deque
     */
    fun previous() {
        if (hasPrevious()) {
            val previousMediaItem = exoPlayer.currentMediaItem
            exoPlayer.seekToPrevious()
            if (previousMediaItem != exoPlayer.currentMediaItem) {
                exoPlayer.prepare()
                musicPlayingIndex--
                musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
                hasNext.value = hasNext()
                hasPrevious.value = hasPrevious()
            }
            exoPlayer.play()
            isPlaying.value = true
        }
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
            REPEAT_MODE_OFF -> {
                repeatMode.value = REPEAT_MODE_ALL
            }

            REPEAT_MODE_ALL -> {
                repeatMode.value = REPEAT_MODE_ONE
            }

            REPEAT_MODE_ONE -> {
                repeatMode.value = REPEAT_MODE_OFF
            }
        }
        exoPlayer.repeatMode = repeatMode.value
    }

    /**
     * Mix the playlist from the playing music
     */
    fun switchShuffleMode() {
        musicQueueToPlay.remove(musicPlaying.value)
        musicQueueToPlay.shuffle()
        musicQueueToPlay.addFirst(musicPlaying.value!!)
        musicPlayingIndex = 0
        loadMusic()
    }
}