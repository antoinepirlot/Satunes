/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.internet.subsonic

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.internet.InternetManager
import io.github.antoinepirlot.satunes.internet.exceptions.NotConnectedException
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.GetAlbumCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.GetIndexesCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.GetMusicFoldersCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.GetRandomMusicCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.PingCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.SubsonicState
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.SubsonicCallback
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author Antoine Pirlot 03/09/2025
 */
@RequiresApi(Build.VERSION_CODES.M)
class SubsonicApiRequester(
    url: String,
    private val username: String,
    private val md5Password: String,
    private val onSubsonicStateChanged: (SubsonicState) -> Unit
) {
    companion object {
        val DEFAULT_STATE: SubsonicState = SubsonicState.DISCONNECTED
        const val SONG_MEDIA_TYPE = "song"
        private const val CLIENT_NAME = "Satunes"
        private const val JSON_FORMAT = "json"
        private var version: String? = null
        internal var status: String? = null
        internal var type: String? = null
        internal var serverVersion: String? = null
        internal var openSubsonic: Boolean? = null
    }

    val url: String = "$url/rest"

    var subsonicState: SubsonicState = DEFAULT_STATE
        internal set(value) {
            field = value
            onSubsonicStateChanged.invoke(field)
        }

    val inUrlMandatoryParams: String
        get() = "u=$username&t=$md5Password&c=$CLIENT_NAME&v=$version&f=$JSON_FORMAT"

    /**
     * Returns the url as https://example.org/rest/[command]?[inUrlMandatoryParams]&[parameters]
     *
     * @param command the command as [String]. For example getSong
     * @param parameters the parameters of command. For example: id=59feo8
     */
    internal fun getCommandUrl(command: String, vararg parameters: String): String {
        var toReturn = "$url/$command?$inUrlMandatoryParams"
        for (parameter: String in parameters)
            toReturn += "&$parameter"
        return toReturn
    }

    internal fun updateVersion(version: String) {
        if (version == Companion.version) return
        Companion.version = version
    }


    /**
     * Create and start a request
     *
     * @param url the request's url
     * @param resCallback the callBack object matching the request.
     * @param newState the new state the [subsonicState] must have.
     */
    private fun get(
        context: Context,
        url: String,
        resCallback: SubsonicCallback,
        newState: SubsonicState = SubsonicState.REQUESTING
    ) {
        this.subsonicState = newState
        if(newState != SubsonicState.PINGING) SubsonicState.DATA_RECEIVED.prepareDataReceived(subsonicCallback = resCallback)
        val client = OkHttpClient()
        val req: Request = Request.Builder()
            .get()
            .url(url)
            .build()
        client.newCall(req).enqueue(responseCallback = resCallback)
    }

    /**
     * Ping API
     */
    fun ping(context: Context, onSucceed: (() -> Unit)? = null) {
        this.get(
            context = context,
            url = this.getCommandUrl(command = "ping", parameters = arrayOf()),
            resCallback = PingCallback(subsonicApiRequester = this, onSucceed = onSucceed),
            newState = SubsonicState.PINGING
        )
    }

    /**
     * Change [subsonicState] to [SubsonicState.DISCONNECTED] if it can be changed.
     *
     * @return true if the process has been successfully done, otherwise false.
     */
    fun disconnect(context: Context): Boolean {
        return when(this.subsonicState) {
            SubsonicState.REQUESTING, SubsonicState.PINGING, SubsonicState.DATA_RECEIVED -> false
            else -> {
                this.subsonicState = SubsonicState.DISCONNECTED
                true
            }
        }
    }

    /**
     * Checks if the internet connection is valid and if another process is not already requesting.
     *
     * @return true if a new request can be done, otherwise false.
     */
    private fun canMakeRequest(context: Context): Boolean {
        if (!InternetManager(context = context).isConnected())
            throw NotConnectedException("Internet connection KO")
        return when (this.subsonicState) {
            SubsonicState.DISCONNECTED -> true
            else -> false
        }
    }

    /**
     * Get randomly [size] musics.
     *
     * @param size the number of music to get (default 10, max 500).
     *
     * @return musics //TODO
     */
    fun loadRandomSongs(context: Context, size: Int = 10, onSucceed: (() -> Unit)? = null) {
        if (size < 1 || size > 500)
            throw IllegalArgumentException("Can't get $size musics")
        this.get(
            context = context,
            url = this.getCommandUrl(
                command = "getRandomSongs",
                parameters = arrayOf("size=$size")
            ),
            resCallback = GetRandomMusicCallback(subsonicApiRequester = this, onSucceed = onSucceed)
        )
    }

    /**
     * Load all data from server.
     * (Not recommended if the server is not the personal one as it could have a lot of data).
     *
     * It gets data in this order:
     *      * MusicFolders -> Music Folder
     *      * Indexes -> Artists
     *      * Single Artist from Indexes -> Albums
     *      * Single Album from Artist -> Songs
     *      * Songs from album
     *
     * @param context the [Context] of the app.
     */
    fun loadAll(context: Context) {
        this.loadMusicFolders(context = context)
    }

    /**
     * Request "getIndexes" with "musicFolderId" param for each folder in [foldersToIndex].
     * Then all folder will received their data.
     */
    private fun loadIndexesByFolder(context: Context) {
        if(!DataManager.hasSubsonicFolders()) return
        for (folder: Folder in DataManager.getRootSubsonicFolders()) {
            this.get(
                context = context,
                url = this@SubsonicApiRequester.getCommandUrl(
                    command = "getIndexes",
                    parameters = arrayOf("musicFolderId=${folder.subsonicId}")
                ),
                resCallback = GetIndexesCallback(
                    subsonicApiRequester = this@SubsonicApiRequester,
                    onSucceed = { this.loadAlbums(context = context) }
                ),
            )
        }
    }

    /**
     * Load albums  query based on each subsonic artist.
     */
    private fun loadAlbums(context: Context) {
        if(!DataManager.hasSubsonicArtists()) return
        for(artist: Artist in DataManager.getSubsonicArtistSet()) {
            this.loadArtist(context = context, artistId = artist.subsonicId!!)
        }
    }

    /**
     * Load artist's information containing albums by using "getArtist" query.
     */
    fun loadArtist(context: Context, artistId: String) {
        this.get(
            context = context,
            url = this.getCommandUrl(command = "getArtist", parameters = arrayOf("id=$artistId")),
            resCallback = GetAlbumCallback(
                subsonicApiRequester = this,
                onSucceed = { this.loadSongs(context = context) }
            )
        )
    }

    /**
     * Load songs of received albums.
     */
    private fun loadSongs(context: Context) {
        if(!DataManager.hasSubsonicAlbums()) return
        for(album: Album in DataManager.getSubsonicAlbumsSet()) {
            this.loadAlbum(context = context, albumId = album.subsonicId!!)
        }
    }

    /**
     * Load album by its id and get songs of it.
     */
    fun loadAlbum(context: Context, albumId: String) {
        this.get(
            context = context,
            url = this.getCommandUrl(command = "getAlbum", parameters = arrayOf("id=$albumId")),
            resCallback = GetAlbumCallback(subsonicApiRequester = this)
        )
    }

    private fun loadMusicFolders(context: Context) {
        this.get(
            context = context,
            url = this.getCommandUrl(command = "getMusicFolders", parameters = arrayOf()),
            resCallback = GetMusicFoldersCallback(
                subsonicApiRequester = this,
                onSucceed = { this.loadIndexesByFolder(context = context) }
            )
        )
    }
}