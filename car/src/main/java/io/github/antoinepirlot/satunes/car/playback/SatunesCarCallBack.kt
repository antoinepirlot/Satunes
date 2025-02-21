/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.car.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import io.github.antoinepirlot.satunes.car.pages.ScreenPages
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 16/03/2024
 */
internal object SatunesCarCallBack : MediaSessionCompat.Callback() {
    internal const val ACTIONS_ON_PLAY: Long =
        ACTION_PAUSE or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SEEK_TO
    internal const val ACTIONS_ON_PAUSE: Long =
        ACTION_PLAY or ACTION_SKIP_TO_NEXT or ACTION_SKIP_TO_PREVIOUS or ACTION_SEEK_TO

    internal const val ACTION_SHUFFLE = "ACTION_SHUFFLE"
    internal const val ACTION_REPEAT = "ACTION_REPEAT"
    internal const val ACTION_LIKE = "ACTION_LIKE"

    override fun onPlay() {

        if (!PlaybackManager.isLoaded.value) {
            return
        }
        PlaybackManager.play(context = SatunesCarMusicService.instance.applicationContext)
    }

    override fun onPause() {
        PlaybackManager.pause(context = SatunesCarMusicService.instance.applicationContext)
    }

    override fun onSkipToQueueItem(queueId: Long) {
        PlaybackManager.seekTo(
            context = SatunesCarMusicService.instance.applicationContext,
            musicId = queueId
        )
    }

    override fun onSeekTo(position: Long) {
        PlaybackManager.seekTo(
            context = SatunesCarMusicService.instance.applicationContext,
            positionMs = position
        )
    }

    override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
        var musicToPlay: Music? = null
        if (mediaId == SatunesCarMusicService.SHUFFLE_ID)
            RouteManager.setShuffleButtonSelected(selected = true)
        else
            musicToPlay = DataManager.getMusic(id = mediaId.toLong())
        loadMusic(musicToPlay = musicToPlay)
        PlaybackManager.start(
            context = SatunesCarMusicService.instance.applicationContext,
            musicToPlay = musicToPlay
        )
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        super.onPlayFromSearch(query, extras)
    }

    override fun onSkipToNext() {
        PlaybackManager.playNext(context = SatunesCarMusicService.instance.applicationContext)
    }

    override fun onSkipToPrevious() {
        PlaybackManager.playPrevious(context = SatunesCarMusicService.instance.applicationContext)
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
        when (action) {
            ACTION_SHUFFLE -> switchShuffleMode()
            ACTION_REPEAT -> switchRepeatMode()
            ACTION_LIKE -> likePlayingMusic()
        }
        //Update playback state from here as no listener function is called for this action.
        val state: Int =
            if (PlaybackManager.isPlaying.value) STATE_PLAYING else STATE_PAUSED
        val actions: Long =
            if (PlaybackManager.isPlaying.value) ACTIONS_ON_PLAY else ACTIONS_ON_PAUSE
        SatunesPlaybackListener.updatePlaybackState(state = state, actions = actions)
    }

    private fun likePlayingMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            PlaybackManager.musicPlaying.value!!.switchLike()
        }
    }

    private fun switchShuffleMode() {
        PlaybackManager.switchShuffleMode(context = SatunesCarMusicService.instance.applicationContext)
    }

    private fun switchRepeatMode() {
        PlaybackManager.switchRepeatMode(context = SatunesCarMusicService.instance.applicationContext)
    }

    /**
     * Load music from the last route deque route.
     */
    private fun loadMusic(musicToPlay: Music? = null) {
        val selectedTab: ScreenPages = RouteManager.getSelectedTab()
        val selectedMediaImpl: MediaImpl? = RouteManager.getSelectedMediaImpl()
        val musicSet: Set<Music>
        if (selectedMediaImpl != null) {
            musicSet = when (selectedTab) {
                ScreenPages.ALL_FOLDERS -> selectedMediaImpl.getMusicSet()
                ScreenPages.ALL_ALBUMS -> selectedMediaImpl.getMusicSet()
                ScreenPages.ALL_ARTISTS -> selectedMediaImpl.getMusicSet()
                ScreenPages.ALL_GENRES -> selectedMediaImpl.getMusicSet()
                ScreenPages.ALL_PLAYLISTS -> selectedMediaImpl.getMusicSet()
                else -> throw UnsupportedOperationException()
            }
        } else {
            val musics: MutableSet<Music> = mutableSetOf()
            when (RouteManager.getSelectedTab()) {
                ScreenPages.ALL_FOLDERS -> DataManager.getFolderSet().forEach { folder: Folder ->
                    musics.addAll(elements = folder.getMusicSet())
                }

                ScreenPages.ALL_ARTISTS ->
                    DataManager.getArtistSet().forEach { artist: Artist ->
                        musics.addAll(elements = artist.getMusicSet())
                    }

                ScreenPages.ALL_ALBUMS ->
                    DataManager.getAlbumSet().forEach { album: Album ->
                        musics.addAll(elements = album.getMusicSet())
                    }

                ScreenPages.ALL_GENRES -> DataManager.getGenreSet().forEach { genre: Genre ->
                    musics.addAll(elements = genre.getMusicSet())
                }

                ScreenPages.ALL_PLAYLISTS -> DataManager.getPlaylistSet()
                    .forEach { playlist: Playlist ->
                        musics.addAll(elements = playlist.getMusicSet())
                    }

                else -> throw UnsupportedOperationException()
            }
            musicSet = musics.toSet()
        }
        if (RouteManager.isShuffleButtonSelected())
            PlaybackManager.loadMusics(
                context = SatunesCarMusicService.instance.applicationContext,
                musics = musicSet,
                shuffleMode = RouteManager.isShuffleButtonSelected(),
                musicToPlay = musicToPlay
            )
        else
            PlaybackManager.loadMusics(
                context = SatunesCarMusicService.instance.applicationContext,
                musics = musicSet,
                musicToPlay = musicToPlay
            )
    }
}