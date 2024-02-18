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

class PlaybackControllerToReplace private constructor(
    context: Context,
    sessionToken: SessionToken,
    musicMediaItemSortedMap: SortedMap<Music, MediaItem>,
) {
    private lateinit var mediaController: MediaController

    private var playlist: Playlist
    private var musicPlayingIndex: Int = PlaybackController.DEFAULT_MUSIC_PLAYING_INDEX
    private var isEnded: Boolean = false
    private var isLoaded: Boolean = PlaybackController.DEFAULT_IS_LOADED
    // Mutable var are used in ui, it needs to be recomposed
    // I use mutable to avoid using function with multiples params like to add listener
    var musicPlaying: MutableState<Music?> = mutableStateOf(DEFAULT_MUSIC_PLAYING)
    var isPlaying: MutableState<Boolean> = mutableStateOf(DEFAULT_IS_PLAYING_VALUE)
    var repeatMode: MutableState<Int> = mutableIntStateOf(DEFAULT_REPEAT_MODE)
    val isShuffle: MutableState<Boolean>
    var hasNext: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_NEXT)
    var hasPrevious: MutableState<Boolean> = mutableStateOf(DEFAULT_HAS_PREVIOUS)

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
                PlaybackController.isPlaying.value = false
                isEnded = true
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            if (PlaybackController.isShuffle.value) {
                // Deactivate shuffle mode
                backToOriginalPlaylist()
            } else {
                // Activate shuffle mode
                shuffle()
            }
            PlaybackController.isShuffle.value = shuffleModeEnabled
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            PlaybackController.isPlaying.value = playWhenReady
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            if (PlaybackController.musicPlaying.value == null) {
                //Fix issue while loading for the first time
                return
            }
            if (mediaItem != PlaybackController.musicPlaying.value!!.mediaItem!!) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK
                    || reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO
                ) {
                    val previousMusic: Music =
                        if (musicPlayingIndex > 0) musicQueueToPlay[musicPlayingIndex - 1]
                        else musicQueueToPlay.last()

                    if (mediaItem == previousMusic.mediaItem!!) {
                        // Previous button has been clicked
                        when (PlaybackController.repeatMode.value) {
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
                        when (PlaybackController.repeatMode.value) {
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
            PlaybackController.musicPlaying.value = musicQueueToPlay[musicPlayingIndex]
            PlaybackController.hasNext.value = hasNext()
            PlaybackController.hasPrevious.value = hasPrevious()
            mediaController.play()
        }
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

        private lateinit var instance: PlaybackControllerToReplace

        /**
         * Return only one instance of MediaController. If there's no instance already created
         * you have to send a context to the constructor otherwise send null.
         *
         * @param context a context if no instance exists, null otherwise
         *
         * @return the instance of MediaController
         */
        fun getInstance(): PlaybackControllerToReplace {
            if (!Companion::instance.isInitialized) {
                throw IllegalStateException("The PlayBackController has not been initialized")
            }
            return instance
        }

        fun initInstance(
            context: Context,
            musicMediaItemSortedMap: SortedMap<Music, MediaItem>
        ): PlaybackControllerToReplace {
            if (!Companion::instance.isInitialized) {
                val sessionToken =
                    SessionToken(context, ComponentName(context, PlaybackService::class.java))
                instance = PlaybackControllerToReplace(
                    context = context,
                    sessionToken = sessionToken,
                    musicMediaItemSortedMap = musicMediaItemSortedMap,
                )
            }
            return getInstance()
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
    fun loadMusic(musicMediaItemSortedMap: SortedMap<Music, MediaItem>, shuffleMode: Boolean = false) {
        if (this.isShuffle.value) {
            this.shuffle()
        }
        this.mediaController.clearMediaItems()
        this.mediaController.addMediaItems(musicMediaItemSortedMap.values.toList())
        mediaController.addListener(listener)
        mediaController.prepare()
        isLoaded = true
    }

    fun isLoaded(): Boolean {
        return this.isLoaded
    }

    fun shuffle() {
        this.playlist.shuffle()
        if (this.isShuffle.value) { }

        var startIndex = 1
        if (this.musicPlaying.value == null) {
            mediaController.clearMediaItems()
            startIndex = DEFAULT_MUSIC_PLAYING_INDEX
        } else {
            mediaController.moveMediaItem(musicPlayingIndex, startIndex)
            mediaController.removeMediaItems(1, mediaController.mediaItemCount)
            musicQueueToPlay.remove(PlaybackController.musicPlaying.value)
        }
        musicQueueToPlay.shuffle()
        if (PlaybackController.musicPlaying.value != null) {
            musicQueueToPlay.addFirst(PlaybackController.musicPlaying.value!!)
        }
        musicPlayingIndex = PlaybackController.DEFAULT_MUSIC_PLAYING_INDEX

        for (i: Int in startIndex..<musicQueueToPlay.size) {
            val music = musicQueueToPlay[i]
            val mediaItem = music.mediaItem
            mediaController.addMediaItem(mediaItem)
            mediaController.prepare()
        }
    }
}