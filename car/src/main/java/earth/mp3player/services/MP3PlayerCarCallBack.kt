/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.services

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import earth.mp3player.models.Album
import earth.mp3player.models.Artist
import earth.mp3player.models.Folder
import earth.mp3player.models.Genre
import earth.mp3player.models.Music
import earth.mp3player.pages.ScreenPages
import earth.mp3player.services.data.DataManager
import earth.mp3player.services.playback.PlaybackController
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 16/03/2024
 */
object MP3PlayerCarCallBack : MediaSessionCompat.Callback() {
    private const val ACTIONS_ON_PLAY: Long = ACTION_PAUSE or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SEEK_TO
    private const val ACTIONS_ON_PAUSE: Long = ACTION_PLAY or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SEEK_TO

    //TODO
    override fun onPlay() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.play()
        setPlaybackState(state = STATE_PLAYING, actions = ACTIONS_ON_PLAY)
    }

    override fun onPause() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.pause()
        setPlaybackState(state = STATE_PAUSED, actions = ACTIONS_ON_PAUSE)
    }

    override fun onSkipToQueueItem(queueId: Long) {}

    override fun onSeekTo(position: Long) {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.seekTo(positionMs = position)
    }

    override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
        val id: Long = mediaId.toLong()
        val music: Music = DataManager.musicMediaItemSortedMap.keys.first { it.id == id }
        this.loadMusic()
        PlaybackController.getInstance().start(musicToPlay = music)
        this.updateMediaPlaying()
        setPlaybackState(state = STATE_PLAYING, actions = ACTIONS_ON_PLAY)
    }

    override fun onSkipToNext() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.playNext()
        this.updateMediaPlaying()
        this.setPlaybackState(state = STATE_PLAYING, actions = ACTIONS_ON_PLAY)
    }

    override fun onSkipToPrevious() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.playPrevious()
        this.updateMediaPlaying()
        this.setPlaybackState(state = STATE_PLAYING, actions = ACTIONS_ON_PLAY)
    }

    private fun updateMediaPlaying() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        val music: Music = playbackController.musicPlaying.value!!
        val mediaSession: MediaSessionCompat = MP3PlayerCarMusicService.session
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, music.id.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, music.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, music.artist?.title)
                .build()
        )
    }

    /**
     * Update the session playback.
     *
     * @param state the state of playback.
     * @param actions to run if clicked on button in playback screen.
     */
    private fun setPlaybackState(state: Int, actions: Long) {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        val musicPlaying: Music = playbackController.musicPlaying.value!!
        val currentPosition: Long = playbackController.getCurrentPosition()
        val extras: Bundle = Bundle()
        extras.putString(
            MediaConstants.PLAYBACK_STATE_EXTRAS_KEY_MEDIA_ID,
            musicPlaying.id.toString()
        )

        val playbackState: PlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(state, currentPosition, 1F)
            .setActions(actions)
            .setActiveQueueItemId(musicPlaying.id)
            .setExtras(extras)
            .build()
        MP3PlayerCarMusicService.session.setPlaybackState(playbackState)
    }

    /**
     * Load music from the last route deque route.
     */
    private fun loadMusic() {
        MP3PlayerCarMusicService.updateQueue()
        val routeDeque: RouteDeque = MP3PlayerCarMusicService.routeDeque

        val lastRoute: String = routeDeque.last()

        val playbackController: PlaybackController = PlaybackController.getInstance()

        if (lastRoute == ScreenPages.ROOT.id || lastRoute == ScreenPages.ALL_MUSICS.id) {
            playbackController.loadMusic(musicMediaItemSortedMap = DataManager.musicMediaItemSortedMap)
            return
        }

        val mediaId: Long = lastRoute.toLong()

        val musicMediaItemSortedMap: SortedMap<Music, MediaItem> =
            when (routeDeque.oneBeforeLast()) {
                ScreenPages.ALL_FOLDERS.id -> {
                    //Current folder has to be loaded (music)
                    val folder: Folder = DataManager.folderMap[mediaId]!!
                    folder.musicMediaItemSortedMap
                }

                ScreenPages.ALL_ALBUMS.id -> {
                    val album: Album = DataManager.albumMap.values.first { it.id == mediaId }
                    album.musicMediaItemSortedMap
                }

                ScreenPages.ALL_ARTISTS.id -> {
                    val artist: Artist = DataManager.artistMap.values.first { it.id == mediaId }
                    artist.musicMediaItemSortedMap
                }

                ScreenPages.ALL_GENRES.id -> {
                    val genre: Genre = DataManager.genreMap.values.first { it.id == mediaId }
                    genre.musicMediaItemSortedMap
                }

                else -> {
                    DataManager.musicMediaItemSortedMap
                }
            }
        playbackController.loadMusic(musicMediaItemSortedMap = musicMediaItemSortedMap)
    }
}