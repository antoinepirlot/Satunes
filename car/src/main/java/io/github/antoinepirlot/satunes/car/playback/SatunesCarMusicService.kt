/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.car.playback

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.QueueItem
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.core.graphics.drawable.toBitmap
import androidx.media.MediaBrowserServiceCompat
import io.github.antoinepirlot.satunes.car.R
import io.github.antoinepirlot.satunes.car.pages.ScreenPages
import io.github.antoinepirlot.satunes.car.pages.pages
import io.github.antoinepirlot.satunes.car.utils.buildMediaItem
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.services.data.DataLoader
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.playback.services.PlaybackManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import io.github.antoinepirlot.satunes.icons.R as RIcons

/**
 * @author Antoine Pirlot on 16/03/2024
 */
internal class SatunesCarMusicService : MediaBrowserServiceCompat() {

    private lateinit var _logger: SatunesLogger

    companion object {
        internal lateinit var instance: SatunesCarMusicService
        private const val MAX_SIZE: Int = 300
        lateinit var session: MediaSessionCompat

        private val loadedQueueItemList: MutableList<QueueItem> = mutableListOf()

        const val SHUFFLE_ID: String = "shuffle"

        fun updateQueue() {
            session.setQueue(loadedQueueItemList)
        }

        fun resetQueue() {
            loadedQueueItemList.clear()
        }

        /**
         * Add the mediaImpl to the queue by creating a mediaImpl item.
         *
         * @return the newly created mediaImpl item.
         */
        internal fun addToQueue(media: MediaImpl): MediaItem {
            val mediaItem: MediaItem = buildMediaItem(media = media)
            if (media is Music) {
                val queueItem = QueueItem(mediaItem.description, media.id)
                loadedQueueItemList.add(queueItem)
            }
            return mediaItem
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        SatunesLogger.DOCUMENTS_PATH =
            applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.path
        _logger = SatunesLogger.getLogger()
        _logger.info("Android Auto is Starting")
        val className: String = this.javaClass.name.split(".").last()
        session = MediaSessionCompat(this, className)
        sessionToken = session.sessionToken
        session.setCallback(SatunesCarCallBack)

        RouteManager.reset()
        runBlocking {
            SettingsManager.loadSettings(context = applicationContext)
        }
        loadAllPlaybackData()
    }

    private fun loadAllPlaybackData() {
        if (!DataLoader.isLoaded.value && !DataLoader.isLoading.value) {
            DataLoader.resetAllData()
            DataLoader.loadAllData(context = baseContext)
        }
        PlaybackManager.checkPlaybackController(
            context = applicationContext,
            listener = SatunesPlaybackListener,
        )
        runBlocking {
            while (!DataLoader.isLoaded.value) {
                delay(50) //Wait (use delay to reduce cpu usage)
            }
        }
        if (!DataLoader.isLoaded.value) {
            val message = "Data has not been loaded"
            _logger.severe(message)
            throw IllegalStateException(message)
        }

        SatunesPlaybackListener.updateMediaPlaying()
        if (PlaybackManager.isPlaying.value) {
            SatunesPlaybackListener.updatePlaybackState(
                state = STATE_PLAYING,
                actions = SatunesCarCallBack.ACTIONS_ON_PLAY
            )
        } else {
            SatunesPlaybackListener.updatePlaybackState(
                state = STATE_PAUSED,
                actions = SatunesCarCallBack.ACTIONS_ON_PAUSE
            )
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(ScreenPages.ROOT.id, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<List<MediaItem>>) {
        val children: List<MediaItem>
        when (parentId) {
            ScreenPages.ROOT.id -> {
                children = getHomeScreenBars()
            }

            ScreenPages.ALL_FOLDERS.id -> {
                RouteManager.reset()
                RouteManager.selectMedia(parentId)
                children = getAllMediaItem(mediaList = DataManager.getFolderSet())
            }

            ScreenPages.ALL_ARTISTS.id -> {
                RouteManager.reset()
                RouteManager.selectMedia(parentId)
                children = getAllMediaItem(mediaList = DataManager.getArtistSet())
            }

            ScreenPages.ALL_ALBUMS.id -> {
                RouteManager.reset()
                RouteManager.selectMedia(parentId)
                children = getAllMediaItem(mediaList = DataManager.getAlbumSet())
            }

            ScreenPages.ALL_GENRES.id -> {
                RouteManager.reset()
                RouteManager.selectMedia(parentId)
                children = getAllMediaItem(mediaList = DataManager.getGenreSet())
            }

            ScreenPages.ALL_MUSICS.id -> {
                RouteManager.reset()
                RouteManager.selectMedia(parentId)
                children = getAllMediaItem(mediaList = DataManager.getMusicSet())
            }

            ScreenPages.ALL_PLAYLISTS.id -> {
                RouteManager.reset()
                RouteManager.selectMedia(parentId)
                children = getAllMediaItem(mediaList = DataManager.getPlaylistSet())
            }

            else -> {
                //When a music is selected, loadChildren is not called, so it's never a music
                RouteManager.selectMedia(parentId)
                children = getAllMediaItem(mediaId = parentId.toLong())
            }
        }
        result.sendResult(children)
    }

    private fun getShuffleButton(): MediaItem {
        val icon: Bitmap = this.getDrawable(RIcons.drawable.white_shuffle_off)!!.toBitmap()

        return buildMediaItem(
            id = SHUFFLE_ID,
            description = "Shuffle Button",
            title = this.getString(R.string.shuffle),
            icon = icon,
            flags = MediaItem.FLAG_PLAYABLE
        )
    }

    private fun getHomeScreenBars(): MutableList<MediaItem> {
        val children: MutableList<MediaItem> = mutableListOf()
        for (page: ScreenPages in pages) {
            val mediaItem: MediaItem = buildMediaItem(
                id = page.id,
                description = page.description,
                title = if (page.titleId == null) ScreenPages.ROOT.id else this.getString(page.titleId),
                flags = MediaItem.FLAG_BROWSABLE
            )
            children.add(mediaItem)
        }
        return children
    }

    /**
     * Get a list of media item based on the media.
     *
     * It creates all MediaItem from all media, if it is a music then it is playable, otherwise
     * it is browsable.
     *
     * Also add these media items as queue item to prepare the queue (don't apply).
     *
     * @param mediaList the media list that contains all media to transform to MediaItem
     *
     * @return a mutable list of MediaItem
     */
    private fun getAllMediaItem(mediaList: Collection<MediaImpl>): List<MediaItem> {
        val mediaItemList: MutableList<MediaItem> = mutableListOf()
        if (mediaList.isEmpty()) {
            return mediaItemList
        }
        mediaItemList.add(getShuffleButton())
        for (media: MediaImpl in mediaList) {
            if (mediaItemList.size >= MAX_SIZE) {
                break // Do not add more than 300 media as it could make android auto bugging
            }
            if (media !is Music && (media.getMusicSet().isEmpty())) {
                continue
            }
            val mediaItem: MediaItem = buildMediaItem(media = media)
            mediaItemList.add(mediaItem)
        }
        return mediaItemList
    }

    /**
     * Get the media from the media referenced, assume that mediaId is never the id of a music.
     * When the route is in pages, then load musics of the right media.
     *
     * @param mediaId the media referenced, it's the media id.
     *
     * @return a mutable list of media item.
     */
    private fun getAllMediaItem(mediaId: Long): List<MediaItem> {
        val selectedTab: ScreenPages = RouteManager.getSelectedTab()
        val mediaImpl: MediaImpl? = try {
            when (selectedTab) {
                ScreenPages.ALL_FOLDERS -> DataManager.getFolder(id = mediaId)
                ScreenPages.ALL_ARTISTS -> DataManager.getArtist(id = mediaId)
                ScreenPages.ALL_ALBUMS -> DataManager.getAlbum(id = mediaId)
                ScreenPages.ALL_GENRES -> DataManager.getGenre(id = mediaId)
                ScreenPages.ALL_PLAYLISTS -> DataManager.getPlaylist(id = mediaId)
                else -> null
            }
        } catch (_: NullPointerException) {
            null
        }

        return this.getAllMediaItem(mediaList = mediaImpl?.getMusicSet() ?: mutableListOf())
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (
            !SettingsManager.playbackWhenClosedChecked ||
            !PlaybackManager.isConfigured() ||
            !PlaybackManager.isPlaying.value
        ) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        if (
            !SettingsManager.playbackWhenClosedChecked ||
            !PlaybackManager.isConfigured() ||
            !PlaybackManager.isPlaying.value
        ) {
            session.release()
            super.onDestroy()
        }

    }
}