package earth.mp3.models

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class ExoPlayerManager private constructor(context: Context) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private val musicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicPlaying: Music? = null
    private var musicPlayingIndex: Int = -1

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
    fun startMusic() {
        if (isPlaying()) {
            return
        }
        musicPlaying = getNextMusic()
        exoPlayer.prepare()
        playPause()
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
            stop(reset = false)
            exoPlayer.seekToNext()
            startMusic()
        }
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
     * Add all music from musicMap to the exoPlayer in the same order and configure the manager
     * to the first music to play
     */
    fun loadMusic(musicList: List<Music>) {
        for (music in musicList) {
            if (!musicQueueToPlay.contains(music)) {
                musicQueueToPlay.add(music)
                if (musicPlaying == null) {
                    musicPlaying = music
                    musicPlayingIndex = 0
                }

                val mediaItem = MediaItem.fromUri(music.getAbsolutePath())
                exoPlayer.addMediaItem(mediaItem)
            }
        }
        exoPlayer.prepare()
    }

    /**
     * Play the previous music in deque
     */
    fun previous() {
        if (hasPrevious()) {
            stop(reset = false)
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