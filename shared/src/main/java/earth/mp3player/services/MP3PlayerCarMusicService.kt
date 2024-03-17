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

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import earth.mp3player.models.Folder
import earth.mp3player.models.Media
import earth.mp3player.models.Music
import earth.mp3player.pages.ScreenPages
import earth.mp3player.pages.pages
import earth.mp3player.services.data.DataLoader
import earth.mp3player.services.data.DataManager
import earth.mp3player.services.playback.PlaybackController
import earth.mp3player.utils.buildMediaItem


/**
 * This class provides a MediaBrowser through a service. It exposes the media library to a browsing
 * client, through the onGetRoot and onLoadChildren methods. It also creates a MediaSession and
 * exposes it through its MediaSession.Token, which allows the client to create a MediaController
 * that connects to and send control commands to the MediaSession remotely. This is useful for
 * user interfaces that need to interact with your media session, like Android Auto. You can
 * (should) also use the same service from your app's UI, which gives a seamless playback
 * experience to the user.
 *
 *
 * To implement a MediaBrowserService, you need to:
 *
 *  *  Extend [MediaBrowserServiceCompat], implementing the media browsing
 * related methods [MediaBrowserServiceCompat.onGetRoot] and
 * [MediaBrowserServiceCompat.onLoadChildren];
 *
 *  *  In onCreate, start a new [MediaSessionCompat] and notify its parent
 * with the session"s token [MediaBrowserServiceCompat.setSessionToken];
 *
 *  *  Set a callback on the [MediaSessionCompat.setCallback].
 * The callback will receive all the user"s actions, like play, pause, etc;
 *
 *  *  Handle all the actual music playing using any method your app prefers (for example,
 * [android.media.MediaPlayer])
 *
 *  *  Update playbackState, "now playing" metadata and queue, using MediaSession proper methods
 * [MediaSessionCompat.setPlaybackState]
 * [MediaSessionCompat.setMetadata] and
 * [MediaSessionCompat.setQueue])
 *
 *  *  Declare and export the service in AndroidManifest with an intent receiver for the action
 * android.media.browse.MediaBrowserService
 *
 * To make your app compatible with Android Auto, you also need to:
 *
 *  *  Declare a meta-data tag in AndroidManifest.xml linking to a xml resource
 * with a &lt;automotiveApp&gt; root element. For a media app, this must include
 * an &lt;uses name="media"/&gt; element as a child.
 * For example, in AndroidManifest.xml:
 * &lt;meta-data android:name="com.google.android.gms.car.application"
 * android:resource="@xml/automotive_app_desc"/&gt;
 * And in res/values/automotive_app_desc.xml:
 * &lt;automotiveApp&gt;
 * &lt;uses name="media"/&gt;
 * &lt;/automotiveApp&gt;
 *
 */

/**
 * @author Antoine Pirlot on 16/03/2024
 */
class MP3PlayerCarMusicService : MediaBrowserServiceCompat() {

    private lateinit var session: MediaSessionCompat
    private lateinit var playbackController: PlaybackController
    private val routeDeque: RouteDeque = RouteDeque()

    override fun onCreate() {
        super.onCreate()

        val className: String = this.javaClass.name.split(".").last()
        session = MediaSessionCompat(this, className)
        sessionToken = session.sessionToken
        session.setCallback(MP3PlayerCarCallBack)
        session.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )

        this.routeDeque.resetRouteDeque()

        //Init playback
        playbackController = PlaybackController.initInstance(baseContext)
        if (!DataLoader.isLoaded && !DataLoader.isLoading) {
            DataLoader.loadAllData(baseContext)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        session.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(ScreenPages.ROOT.id, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaItem>>) {
        var children: MutableList<MediaItem>? = null
        when (parentId) {
            ScreenPages.ROOT.id -> {
                this.routeDeque.resetRouteDeque()
                result.sendResult(getHomeScreen())
                return
            }

            ScreenPages.ALL_FOLDERS.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.folderMap.values.toList())
                this.routeDeque.resetRouteDeque()
                this.routeDeque.addLast(parentId)
            }

            ScreenPages.ALL_ARTISTS.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.artistMap.values.toList())
                this.routeDeque.resetRouteDeque()
                this.routeDeque.addLast(parentId)
            }

            ScreenPages.ALL_ALBUMS.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.albumMap.values.toList())
                this.routeDeque.resetRouteDeque()
                this.routeDeque.addLast(parentId)

            }

            ScreenPages.ALL_GENRES.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.genreMap.values.toList())
                this.routeDeque.resetRouteDeque()
                this.routeDeque.addLast(parentId)
            }

            ScreenPages.ALL_MUSICS.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.musicMediaItemSortedMap.keys.toList())
                this.routeDeque.resetRouteDeque()
                this.routeDeque.addLast(parentId)
            }

            else -> {
                //When a music is selected, loadChildren is not called, so it's never a music
                if (this.routeDeque.isEmpty()) {
                    result.sendResult(null)
                    return
                }
                this.routeDeque.addLast(parentId)
                children = this.getAllMediaMediaItemList(mediaId = parentId.toLong())
            }
        }
        result.sendResult(children)
    }

    private fun getHomeScreen(): MutableList<MediaItem> {
        val children: MutableList<MediaItem> = mutableListOf()
        for (page: ScreenPages in pages) {
            val mediaItem: MediaItem = buildMediaItem(
                id = page.id,
                description = page.description,
                title = page.title,
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
     * @param mediaList the media list that contains all media to transform to MediaItem
     *
     * @return a mutable list of MediaItem
     */
    private fun getAllMediaMediaItemList(mediaList: List<Media>): MutableList<MediaItem> {
        val mediaItemList: MutableList<MediaItem> = mutableListOf()
        for (media: Media in mediaList) {
            if (media !is Music && media.musicMediaItemSortedMap.isEmpty()) {
                continue
            }

            val mediaItem: MediaItem = buildMediaItem(media = media)
            mediaItemList.add(mediaItem)
        }
        return mediaItemList
    }

    /**
     * Get the media from the mediaId, assume that mediaId is never the id of a music.
     *
     * @return a mutable list of media item.
     */
    private fun getAllMediaMediaItemList(mediaId: Long): MutableList<MediaItem> {
        val mediaList: MutableList<Media> = mutableListOf()
        when (this.routeDeque.oneBeforeLast()) {
            ScreenPages.ROOT.id -> throw IllegalStateException("An error occurred in the route processing")
            ScreenPages.ALL_FOLDERS.id -> {
                val folder: Folder = DataManager.folderMap.get(key = mediaId)!!
                mediaList.addAll(folder.musicMediaItemSortedMap.keys.toList())
            }

            ScreenPages.ALL_ARTISTS.id -> {
//                mediaList.add(DataManager.artistMap.get(key = mediaId)!!)
            }
        }
        return this.getAllMediaMediaItemList(mediaList = mediaList)
    }
}