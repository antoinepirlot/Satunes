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
import earth.mp3player.models.Album
import earth.mp3player.models.Artist
import earth.mp3player.models.Genre
import earth.mp3player.models.Media
import earth.mp3player.models.Music
import earth.mp3player.pages.ScreenPages
import earth.mp3player.pages.pages
import earth.mp3player.services.data.DataLoader
import earth.mp3player.services.data.DataManager
import earth.mp3player.services.playback.PlaybackController
import earth.mp3player.utils.buildMediaItem

/**
 * @author Antoine Pirlot on 16/03/2024
 */
class MP3PlayerCarMusicService : MediaBrowserServiceCompat() {

    private lateinit var playbackController: PlaybackController

    companion object {
        val routeDeque: RouteDeque = RouteDeque()
        lateinit var session: MediaSessionCompat
    }

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

        routeDeque.resetRouteDeque()

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
                routeDeque.resetRouteDeque()
                result.sendResult(getHomeScreen())
                return
            }

            ScreenPages.ALL_FOLDERS.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.folderMap.values.toList())
                routeDeque.resetRouteDeque()
                routeDeque.addLast(parentId)
            }

            ScreenPages.ALL_ARTISTS.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.artistMap.values.toList())
                routeDeque.resetRouteDeque()
                routeDeque.addLast(parentId)
            }

            ScreenPages.ALL_ALBUMS.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.albumMap.values.toList())
                routeDeque.resetRouteDeque()
                routeDeque.addLast(parentId)

            }

            ScreenPages.ALL_GENRES.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.genreMap.values.toList())
                routeDeque.resetRouteDeque()
                routeDeque.addLast(parentId)
            }

            ScreenPages.ALL_MUSICS.id -> {
                children =
                    getAllMediaMediaItemList(mediaList = DataManager.musicMediaItemSortedMap.keys.toList())
                routeDeque.resetRouteDeque()
                routeDeque.addLast(parentId)
            }

            else -> {
                //When a music is selected, loadChildren is not called, so it's never a music
                if (routeDeque.isEmpty()) {
                    result.sendResult(null)
                    return
                }
                routeDeque.addLast(parentId)
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
     * Get the media from the media referencer, assume that mediaId is never the id of a music.
     * When the route is in pages, then load musics of the right media.
     *
     * @param mediaId the media referencer, it's the media id.
     *
     * @return a mutable list of media item.
     */
    private fun getAllMediaMediaItemList(mediaId: Long): MutableList<MediaItem> {
        var media: Media? = null
        when (routeDeque.oneBeforeLast()) {
            //TODO OPTIMISATION NEEDED (Use media interface)
            ScreenPages.ROOT.id, ScreenPages.ALL_MUSICS.id -> throw IllegalStateException("An error occurred in the route processing")
            ScreenPages.ALL_FOLDERS.id -> media = DataManager.folderMap[mediaId]!!
            ScreenPages.ALL_ARTISTS.id -> {
                //TODO create artist map with id
                DataManager.artistMap.forEach { (_, artist: Artist) ->
                    if (artist.id == mediaId) {
                        media = artist
                        return@forEach
                    }
                }
            }

            ScreenPages.ALL_ALBUMS.id -> {
                //TODO create album map with id
                val id: Long = mediaId.toLong()
                DataManager.albumMap.forEach { (_, album: Album) ->
                    if (album.id == id) {
                        media = album
                        return@forEach
                    }
                }
            }

            ScreenPages.ALL_GENRES.id -> {
                //TODO create genre map with id
                val id: Long = mediaId
                DataManager.genreMap.forEach { (_, genre: Genre) ->
                    if (genre.id == id) {
                        media = genre
                        return@forEach
                    }
                }
            }
        }
        return this.getAllMediaMediaItemList(
            mediaList = media?.musicMediaItemSortedMap?.keys?.toList() ?: mutableListOf()
        )
    }
}