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
import earth.mp3.models.Playlist
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
    val isShuffle: MutableState<Boolean>
    var hasNext: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_NEXT)
    var hasPrevious: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_PREVIOUS)
    var isLoaded: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_LOADED)

    init {
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            this.mediaController = controllerFuture.get()
        }, ContextCompat.getMainExecutor(context))
        this.playlist = Playlist(musicMediaItemSortedMap = musicMediaItemSortedMap)
        this.isShuffle = this.playlist.isShuffle
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
            switchShuffleMode()
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            isPlaying.value = playWhenReady
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (musicPlaying.value == null) {
                //Fix issue while loading for the first time
                return
            }
            if (mediaItem != musicPlaying.value!!.mediaItem) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK
                    || reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
                ) {
                    val previousMusic: Music =
                        if (musicPlayingIndex > 0) playlist.musicList[musicPlayingIndex - 1]
                        else playlist.musicList.last()

                    if (mediaItem == previousMusic.mediaItem) {
                        // Previous button has been clicked
                        when (repeatMode.value) {
                            Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ONE -> {
                                musicPlayingIndex--
                            }

                            Player.REPEAT_MODE_ALL -> {
                                if (previousMusic == playlist.musicList.last()) {
                                    musicPlayingIndex = playlist.musicList.lastIndex
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
                                if (musicPlayingIndex + 1 == playlist.musicList.size) {
                                    musicPlayingIndex = 0
                                } else {
                                    musicPlayingIndex++
                                }
                            }
                        }
                    }
                }
            }
            musicPlaying.value = playlist.musicList[musicPlayingIndex]
            updateHasNext()
            updateHasPrevious()
            mediaController.play()
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
        this.isPlaying.value = true
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
            }
        }
    }

    fun playNext() {
        this.mediaController.seekToNext()
    }

    fun playPrevious() {
        mediaController.seekToPrevious()
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
        this.mediaController.clearMediaItems()
        if (shuffleMode) {
            this.playlist.isShuffle.value = false
            this.switchShuffleMode()
        } else {
            this.mediaController.addMediaItems(musicMediaItemSortedMap.values.toList())
        }
        mediaController.addListener(listener)
        mediaController.prepare()
        isLoaded.value = true
    }

    fun switchShuffleMode() {
        this.playlist.shuffle(this.musicPlaying.value)
        if (this.isShuffle.value) {
            if (this.musicPlaying.value == null) {
                // No music is playing
                this.mediaController.clearMediaItems()
                this.mediaController.addMediaItems(this.playlist.mediaItemList)
            } else {
                // A music is playing
                // Move music playing to the first index and remove other
                this.mediaController.moveMediaItem(
                    this.musicPlayingIndex,
                    DEFAULT_MUSIC_PLAYING_INDEX
                )
                this.mediaController.replaceMediaItems(
                    DEFAULT_MUSIC_PLAYING_INDEX + 1,
                    this.mediaController.mediaItemCount,
                    this.playlist.mediaItemList
                )
                //Add music in shuffle mode except the music playing
                val mediaItems: MutableList<MediaItem> = this.playlist.mediaItemList.toMutableList()
                if (!mediaItems.remove(this.musicPlaying.value!!.mediaItem)) {
                    throw IllegalStateException("The Music playing is not into the playlist")
                }
                this.mediaController.addMediaItems(this.playlist.mediaItemList)
            }
        }
        this.musicPlayingIndex = DEFAULT_MUSIC_PLAYING_INDEX
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