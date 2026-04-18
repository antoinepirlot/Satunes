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

package io.github.antoinepirlot.satunes.database.models.database.tables

import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.database.exceptions.media.LocalMusicNotFoundException
import io.github.antoinepirlot.satunes.database.models.internet.ApiRequester
import io.github.antoinepirlot.satunes.database.models.media.MediaData
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicGenre
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.database.DatabaseManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


/**
 * @author Antoine Pirlot on 30/03/2024
 */

@Serializable
@Entity(
    tableName = "musics",
    indices = [Index(value = ["local_id", "subsonic_id"], unique = true)]
)
internal class MusicDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "music_id") override val id: Long = 0,
    @ColumnInfo(name = "local_id") var localId: Long? = null,
    @ColumnInfo(name = "subsonic_id") var subsonicId: String? = null,
    @ColumnInfo(name = "absolute_path") var absolutePath: String,
) : MediaData {
    @Transient
    @Ignore
    private val _logger: Logger? = Logger.getLogger()

    @Ignore
    @Transient
    override lateinit var title: String

    @ColumnInfo(name = "liked")
    var liked: Boolean = false

    suspend fun getMusic(apiRequester: ApiRequester? = null): Music? {
        return if (this.subsonicId != null) {
            this.toSubsonicMusic(apiRequester = apiRequester)
        } else {
            try {
                try {
                    DataManager.getMusicByPath(absolutePath = absolutePath)
                } catch (_: NullPointerException) {
                    //The path has changed
                    try {
                        val music: Music = DataManager.getMusic(id = id)
                        DatabaseManager.getInstance().updateMusic(music)
                        music
                    } catch (_: LocalMusicNotFoundException) {
                        null
                    }
                }
            } catch (e: Throwable) {
                val message = "An error occurred while getting music in musicDB"
                _logger?.severe(message)
                throw e
            }
        }
    }

    private suspend fun toSubsonicMusic(apiRequester: ApiRequester?): SubsonicMusic? {
        var music: SubsonicMusic? = DataManager.getSubsonicMusic(id = this.subsonicId!!)
        var mustWait: Boolean = true
        if (music == null && apiRequester != null)
            apiRequester.getSong(
                musicId = this.subsonicId!!,
                onDataRetrieved = {
                    music = it
                    //Updating path prevent playing music from server instead of local storage
                    music!!.updateAbsolutePath(absolutePath = this.absolutePath)
                },
                onError = { music = this.buildSubsonicMusic(apiRequester = apiRequester) },
                onFinished = { mustWait = false }
            )
        else mustWait = false
        while (mustWait);
        return music
    }

    /**
     * Read absolutePath's file mp3 tags to create the SubsonicMusic object.
     * Assume subsonicId is not null
     */
    private fun buildSubsonicMusic(apiRequester: ApiRequester): SubsonicMusic? {
        return try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(this.absolutePath)

            val title: String =
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: this.absolutePath
                    .split("/").last() //Keep the file name only
                    .split(".").first() //Remove file extension

            val artist: SubsonicArtist = SubsonicArtist(
                subsonicId = "artist", //TODO
                title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)!!,
                apiRequester = apiRequester
            )
            val music: SubsonicMusic = SubsonicMusic(
                subsonicId = this.subsonicId!!,
                title = title,
                displayName = title,
                absolutePath = this.absolutePath,
                addedDateMs = 0, //TODO
                durationMs = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong(),
                size = 0, //TODO
                folder = DataManager.getSubsonicRootFolder(),
                artist = artist,
                album = SubsonicAlbum(
                    subsonicId = "album",
                    title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)!!,
                    artist = artist,
                    coverArtId = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) mmr.extractMetadata(
                        MediaMetadataRetriever.METADATA_KEY_IMAGE_PRIMARY
                    ) else null, //TODO
                    apiRequester = apiRequester
                ), //TODO
                genre = SubsonicGenre(
                    subsonicId = "genre", //TODO
                    title = "genre", //TODO
                    apiRequester = apiRequester
                ), //TODO
                apiRequester = apiRequester
            )
            DataManager.addMusic(music = music)
            music
        } catch (_: NullPointerException) {
            null
        } catch (_: IllegalArgumentException) {
            _logger?.severe("File not found while building Subsonic Music")
            null
        }
    }
}