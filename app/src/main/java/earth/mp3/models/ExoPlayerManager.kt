package earth.mp3.models

import android.content.Context
import android.os.Environment
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters.AudioOffloadPreferences
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerManager @OptIn(UnstableApi::class) private constructor(context: Context) {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private val musicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicPlaying: Music? = null
    private var musicPlayingIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX
    var isPlaying: MutableState<Boolean> = mutableStateOf(false)
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
            if (musicToPlay == musicPlaying) {
                return
            }
            for (i: Int in 0..<musicQueueToPlay.size) {
                val music = musicQueueToPlay[i]
                if (musicToPlay == music) {
                    musicPlaying = musicToPlay
                    musicPlayingIndex = i
                    break
                }
            }
        } else {
            musicPlayingIndex = 0
            musicPlaying = musicQueueToPlay[musicPlayingIndex]
        }
        exoPlayer.seekTo(musicPlayingIndex, 0)
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
        } else {
            exoPlayer.play()
        }
        switchIsPlaying()
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
            musicPlaying = getNextMusic()
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
        return musicPlaying != musicQueueToPlay.last()
    }

    fun getMusicPlaying(): Music? {
        return musicPlaying
    }

    /**
     * Add all music from musicMap to the exoPlayer in the same order
     */
    fun loadMusic(musicList: List<Music>) {
        clearPlaylist()
        for (music in musicList) {
            if (!musicQueueToPlay.contains(music)) {
                musicQueueToPlay.add(music)
                val mediaItem = MediaItem.fromUri(music.getAbsolutePath())
                exoPlayer.addMediaItem(mediaItem)
            }
        }
    }

    /**
     * Set the exo player to the default state without media items. clear all
     */
    private fun clearPlaylist() {
        musicQueueToPlay.clear()
        exoPlayer.clearMediaItems()
        musicPlaying = null
        musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
        isPlaying.value = false
    }

    fun addListener(
        mutableMusicPlaying: MutableState<Music>,
        mutableHasNext: MutableState<Boolean>,
        mutableHasPrevious: MutableState<Boolean>,
        mutableIsPlaying: MutableState<Boolean>
    ) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    if (musicPlayingIndex + 1 == musicQueueToPlay.size) {
                        //It is listened because the last music has been finished
                        return
                    }
                    musicPlayingIndex++
                    musicPlaying = musicQueueToPlay[musicPlayingIndex]
                    mutableMusicPlaying.value = musicPlaying!!
                    mutableHasNext.value = hasNext()
                    mutableHasPrevious.value = hasPrevious()
                    mutableIsPlaying.value = isPlaying.value
                }
            }
        })
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
                musicPlaying = musicQueueToPlay[musicPlayingIndex]
            }
            exoPlayer.play()
            isPlaying.value = true
        }
    }

    fun hasPrevious(): Boolean {
        return musicPlaying != musicQueueToPlay[0]
    }
}