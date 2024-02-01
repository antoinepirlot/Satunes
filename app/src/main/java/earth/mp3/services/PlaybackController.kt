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

class PlaybackController private constructor(context: Context, sessionToken: SessionToken) {
    private lateinit var mediaController: MediaController

    private val musicMediaItemMap: MutableMap<Music, MediaItem> = mutableMapOf()
    private val mediaItemMusicMap: MutableMap<MediaItem, Music> = mutableMapOf()
    private val originalMusicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicQueueToPlay: ArrayDeque<Music> = ArrayDeque()
    private var musicPlayingIndex: Int = DEFAULT_MUSIC_PLAYING_INDEX
    private var isEnded: Boolean = false

    // Mutable var are used in ui, it needs to be recomposed
    // I use mutable to avoid using function with multiples params like to add listener
    var musicPlaying: MutableState<Music?> = mutableStateOf(null)
    var isPlaying: MutableState<Boolean> = mutableStateOf(false)
    var repeatMode: MutableState<Int> = mutableIntStateOf(Player.REPEAT_MODE_OFF)
    var shuffleMode: MutableState<Boolean> = mutableStateOf(false)
    var hasNext: MutableState<Boolean> = mutableStateOf(false)
    var hasPrevious: MutableState<Boolean> = mutableStateOf(false)

    init {
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
        }, ContextCompat.getMainExecutor(context))
    }

    companion object {
        val ROOT_PATH: String = Environment.getExternalStorageDirectory().path
        const val DEFAULT_MUSIC_PLAYING_INDEX = -1

        private lateinit var instance: PlaybackController

        /**
         * Return only one instance of MediaController. If there's no instance already created
         * you have to send a context to the constructor otherwise send null.
         *
         * @param context a context if no instance exists, null otherwise
         *
         * @return the instance of MediaController
         */
        fun getInstance(context: Context? = null): PlaybackController {
            if (!Companion::instance.isInitialized) {
                if (context == null) {
                    throw IllegalStateException("The context can't be null when instance is not init")
                }
                val sessionToken =
                    SessionToken(context, ComponentName(context, PlaybackService::class.java))
                instance = PlaybackController(context, sessionToken)
            }
            return instance
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

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                when (repeatMode.value) {
                    Player.REPEAT_MODE_OFF -> {
                        musicPlayingIndex++
                        musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
                        hasNext.value = hasNext()
                        hasPrevious.value = hasPrevious()
                    }

                    Player.REPEAT_MODE_ALL -> {
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

    /**
     * Start the music in params
     * If music to play is null, play the first music of the playlist otherwise play the musicToPlay
     * If the music to play is already the music playing nothing is done.
     *
     * @param musicToPlay the music to play
     */
    fun start(musicToPlay: Music? = null, positionMs: Long = 0) {
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
        } else if (musicPlayingIndex < 0) {
            musicPlayingIndex = 0
            musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
        }
        mediaController.seekTo(musicPlayingIndex, positionMs)
        hasNext.value = hasNext()
        hasPrevious.value = hasPrevious()
        mediaController.prepare()
        mediaController.play()
        switchIsPlaying()
    }

    /**
     * Play music if it is paused otherwise start music
     */
    fun playPause() {
        if (isPlaying.value) {
            mediaController.pause()
            switchIsPlaying()
        } else {
            if (isEnded) {
                start()
                isEnded = false
            } else {
                mediaController.play()
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
            mediaController.seekToNext()
            musicPlaying.value = getNextMusic()
            hasNext.value = hasNext()
            hasPrevious.value = hasPrevious()
            mediaController.prepare()
            mediaController.play()
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
                    musicMediaItemMap[music] = mediaItem
                    mediaItemMusicMap[mediaItem] = music
                    mediaController.addMediaItem(mediaItem)
                }
            }
            mediaController.addListener(listener)
        } else {
            mediaController.clearMediaItems()
            for (i: Int in 0..<musicQueueToPlay.size) {
                val music = musicQueueToPlay[i]
                if (music == musicPlaying.value) {
                    musicPlayingIndex = i
                }
                val mediaItem = MediaItem.fromUri(music.getAbsolutePath())
                musicMediaItemMap[music] = mediaItem
                mediaItemMusicMap[mediaItem] = music
                mediaController.addMediaItem(mediaItem)
            }
        }
    }

    /**
     * Set the exo player to the default state without media items. clear all
     */
    private fun resetToDefault() {
        mediaController.removeListener(listener)
        originalMusicQueueToPlay.clear()
        musicQueueToPlay.clear()
        mediaController.clearMediaItems()
        musicMediaItemMap.clear()
        musicPlaying.value = null
        musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
        isPlaying.value = false
    }

    /**
     * Play the previous music in deque
     */
    fun previous() {
        val previousMediaItem = mediaController.currentMediaItem
        mediaController.seekToPrevious()
        if (previousMediaItem != mediaController.currentMediaItem) {
            mediaController.prepare()
            musicPlayingIndex--
            musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
            hasNext.value = hasNext()
            hasPrevious.value = hasPrevious()
        }
        mediaController.play()
        isPlaying.value = true
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
        if (shuffleMode.value) {
            // Deactivate shuffle mode
            backToOriginalPlaylist()
            shuffleMode.value = false
        } else {
            // Activate shuffle mode
            shuffle()
            shuffleMode.value = true
        }
    }

    private fun backToOriginalPlaylist() {
        if (musicPlayingIndex == 0) {
            mediaController.removeMediaItems(1, mediaController.mediaItemCount)
        } else if (musicPlayingIndex == musicQueueToPlay.size - 1) {
            mediaController.removeMediaItems(0, mediaController.mediaItemCount - 1)
        } else {
            mediaController.removeMediaItems(musicPlayingIndex + 1, mediaController.mediaItemCount)
            mediaController.removeMediaItems(0, musicPlayingIndex)
        }
        musicQueueToPlay = ArrayDeque(originalMusicQueueToPlay)
        //Update index
        for (i: Int in 0..<musicQueueToPlay.size) {
            val music = musicQueueToPlay[i]
            if (music == musicPlaying.value) {
                musicPlayingIndex = i
                musicPlaying.value = music
                break
            }
        }

        for (i: Int in 0..<musicQueueToPlay.size) {
            if (i == musicPlayingIndex) {
                continue
            }
            val mediaItem = musicMediaItemMap[musicQueueToPlay[i]]!!
            if (i < musicPlayingIndex) {
                mediaController.addMediaItem(i, mediaItem)
            } else {
                mediaController.addMediaItem(mediaItem)
            }
        }

//        if (musicPlayingIndex > 0) {
//            // music playing index is preserved in media controller
//            mediaController.removeMediaItems(0, musicPlayingIndex + 1)
//            mediaController.removeMediaItems(musicPlayingIndex + 1, mediaController.mediaItemCount)
//        }
//        musicQueueToPlay = ArrayDeque(originalMusicQueueToPlay)
//        //update music playing index
//        for (i: Int in 0..< musicQueueToPlay.size - 1) {
//            if (musicPlaying.value == musicQueueToPlay[i]) {
//                musicPlayingIndex = i
//                break
//            }
//        }
    }


    /**
     * Shuffle music
     */
    private fun shuffle() {
        mediaController.moveMediaItem(musicPlayingIndex, 0)
        mediaController.removeMediaItems(1, mediaController.mediaItemCount)
        musicQueueToPlay.remove(musicPlaying.value!!)
        musicQueueToPlay.shuffle()
        musicQueueToPlay.addFirst(musicPlaying.value!!)
        musicPlayingIndex = 0
        for (i: Int in 1..<musicQueueToPlay.size - 1) {
            val music = musicQueueToPlay[i]
            val mediaItem = musicMediaItemMap[music]!!
            mediaController.addMediaItem(mediaItem)
            mediaController.prepare()
        }
    }
}