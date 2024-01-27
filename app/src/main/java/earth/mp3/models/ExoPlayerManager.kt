package earth.mp3.models

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.TrackSelectionParameters.AudioOffloadPreferences
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector

class ExoPlayerManager @OptIn(UnstableApi::class) private constructor(context: Context) {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var trackSelector: TrackSelector
    private val musicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicPlaying: Music? = null
    private var musicPlayingIndex: Int = -1

    init {
        trackSelector = DefaultTrackSelector(context)
        exoPlayer = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()

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
        const val ROOT_PATH: String = "/sdcard"//Environment.getExternalStorageDirectory().path

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
     * Start the first music of the music queue if it's not already playing
     *
     * @throws NoSuchElementException if the queue is empty
     */
//    fun start() {
//        if (isPlaying()) {
//            return
//        }
//        musicPlaying = getNextMusic()
//        exoPlayer.prepare()
//        playPause()
//    }

    /**
     * Start the music in params.
     * If the music to play is already the music playing nothing is done.
     *
     * @param musicToPlay the music to play
     */
    fun start(musicToPlay: Music) {
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
        exoPlayer.seekTo(musicPlayingIndex, 0)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    /**
     * Play music if it is paused otherwise start music
     */
    fun playPause() {
        if (isPlaying()) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    /**
     * Stop the music
     */
    fun stop(reset: Boolean = true) {
        exoPlayer.stop()
        if (reset) {
            musicPlayingIndex = -1
            exoPlayer.release()
            // TODO make sure new one is created
        }
//        exoPlayer.release()
//        exoPlayer = ExoPlayer.Builder(context).build()
    }

    /**
     * Play the next music in queue
     */
    fun next() {
        if (hasNext()) {
            exoPlayer.seekToNext()
            musicPlaying = getNextMusic()
            exoPlayer.prepare()
            playPause()
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
        return musicQueueToPlay.isNotEmpty()
    }

    fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    fun getMusicPlaying(): Music? {
        return musicPlaying
    }

    /**
     * Add all music from musicMap to the exoPlayer in the same order
     */
    fun loadMusic(musicList: List<Music>) {
        for (music in musicList) {
            if (!musicQueueToPlay.contains(music)) {
                musicQueueToPlay.add(music)
                val mediaItem = MediaItem.fromUri(music.getAbsolutePath())
                exoPlayer.addMediaItem(mediaItem)
            }
        }
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
            playPause()
        }
    }

    fun hasPrevious(): Boolean {
        return musicQueueToPlay.isNotEmpty() && musicPlaying != musicQueueToPlay[0]
    }
}