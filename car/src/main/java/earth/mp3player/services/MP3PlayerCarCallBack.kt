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
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
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
    private var mediaIdLoaded: Long? = null

    //TODO
    override fun onPlay() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.play()
        setPlaybackState(state = STATE_PLAYING, action = ACTION_PAUSE)
    }

    override fun onPause() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.pause()
        setPlaybackState(state = STATE_PAUSED, action = ACTION_PLAY)
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

        val mediaSession: MediaSessionCompat = MP3PlayerCarMusicService.session
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Song Name"
                )
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "Artist name"
                )
//                .putString(
//                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
//                    albumArtUri.toString())
                .putLong(
                    MediaConstants.METADATA_KEY_IS_EXPLICIT,
                    MediaConstants.METADATA_VALUE_ATTRIBUTE_PRESENT
                )
                .putLong(
                    MediaDescriptionCompat.EXTRA_DOWNLOAD_STATUS,
                    MediaDescriptionCompat.STATUS_DOWNLOADED
                )
                .build()
        )
        setPlaybackState(musicId = id, state = STATE_PLAYING, action = ACTION_PAUSE)
    }

    /**
     * Update the session playback.
     *
     * @param musicId the music id (from playback library).
     * @param state the state of playback.
     * @param action to run if clicked on button in playback screen.
     */
    private fun setPlaybackState(musicId: Long? = null, state: Int, action: Long) {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        val currentPosition: Long = playbackController.getCurrentPosition()
        val playbackState = if (musicId != null) {
            PlaybackStateCompat.Builder()
                .setState(state, currentPosition, 1F)
                .setActions(action)
                .setActiveQueueItemId(musicId)
        } else {
            PlaybackStateCompat.Builder()
                .setState(state, currentPosition, 1F)
                .setActions(action)
        }
        MP3PlayerCarMusicService.session.setPlaybackState(playbackState.build())
    }

    /**
     * Load music from the last route deque route.
     */
    private fun loadMusic() {
        MP3PlayerCarMusicService.updateQueue()
        val routeDeque: RouteDeque = MP3PlayerCarMusicService.routeDeque

        @Suppress("NAME_SHADOWING")
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

    override fun onSkipToNext() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.playNext()
    }

    override fun onSkipToPrevious() {
        val playbackController: PlaybackController = PlaybackController.getInstance()
        playbackController.playPrevious()
    }

}