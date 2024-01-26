package earth.mp3.models

import android.media.AudioAttributes
import android.media.MediaPlayer

object MediaPlayerManager {
    private val rootPath: String = "/sdcard"//Environment.getExternalStorageDirectory().path
    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    private val musicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicPlaying: Music? = null

    init {
        mediaPlayer!!.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }

    /**
     * Start the first music of the music queue if it's not already playing
     *
     * @param musicList the list of music to be played
     *
     * @throws NoSuchElementException if the queue is empty
     */
    fun startMusic() {
        if (mediaPlayer!!.isPlaying) {
            return
        }
        musicPlaying = getFirstMusic()
        val path = "$rootPath/${musicPlaying!!.relativePath}${musicPlaying!!.name}"
        mediaPlayer!!.apply {
            setDataSource(path)
            prepare()
            start()
        }
    }

    /**
     * get and remove the first music of the array deque
     *
     * @return the first music of the array deque
     * @throws NoSuchElementException if the queue is empty
     */
    private fun getFirstMusic(): Music {
        return musicQueueToPlay.removeFirst()
    }

    /**
     * Play music if it is paused otherwise start music
     */
    fun playPause() {
        if (isPlaying()) {
            mediaPlayer!!.pause()
        } else {
            mediaPlayer!!.start()
        }
    }

    /**
     * Stop the music
     */
    fun stop() {
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        mediaPlayer = MediaPlayer()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer!!.isPlaying
    }

    /**
     * Add all music from musicMap to the queue in the same order
     */
    fun loadMusic(musicMap: Map<Long, Music>) {
        for (music in musicMap.values) {
            if (!musicQueueToPlay.contains(music)) {
                musicQueueToPlay.add(music)
            }
        }
    }
}