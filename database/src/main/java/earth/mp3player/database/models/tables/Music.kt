/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.database.models.tables

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.getSystemService
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import earth.mp3player.database.models.dto.AlbumDTO
import earth.mp3player.database.models.dto.ArtistDTO
import earth.mp3player.database.models.dto.FolderDTO
import earth.mp3player.database.models.dto.GenreDTO
import earth.mp3player.database.models.dto.MusicDTO
import java.io.File

/**
 * @author Antoine Pirlot on 27/03/2024
 */

@Entity(tableName = "musics")
data class Music (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "music_id") override val id: Long,
    @ColumnInfo(name = "title") override val title: String,
    @ColumnInfo(name = "relative_path") override var relativePath: String,
    @ColumnInfo(name = "folder_id") override val folderId: Long,
    @ColumnInfo(name = "genre_id") override var genreId: Long? = null,
    @ColumnInfo(name = "album_id") override val albumId: Long? = null,
    @ColumnInfo(name = "artist_id") override val artistId: Long? = null,
) : MusicDTO {
    @Ignore override lateinit var displayName: String
    @Ignore override var duration: Long = -1
    @Ignore override var size: Int = -1
    @Ignore override var folder: FolderDTO? = null
    @Ignore override var artist: ArtistDTO? = null
    @Ignore override var album: AlbumDTO? = null
    @Ignore override var genre: GenreDTO? = null
    @Ignore lateinit var mediaItem: MediaItem
    @Ignore override var absolutePath: String? = "${ROOT_PATH}/$relativePath/$displayName"
    @Ignore override var uri: Uri = Uri.Builder().appendPath(this.absolutePath).build()
    @Ignore override var artwork: ImageBitmap? = null

    companion object {
        val ROOT_PATH: String = Environment.getExternalStorageDirectory().path
    }

    //Init
    constructor(id: Long, title: String, relativePath: String, folderId: Long,
                genreId: Long? = null, albumId: Long? = null, artistId: Long? = null,
                displayName: String, duration: Long, size: Int, folder: FolderDTO? = null,
                artist: ArtistDTO? = null, album: AlbumDTO? = null, genre: GenreDTO? = null,
                context: Context
    ) : this(
        id = id, title = title, relativePath = relativePath, folderId = folderId, genreId = genreId,
        albumId = albumId, artistId = artistId
    ) {
        this.displayName = displayName
        this.duration = duration
        this.size = size
        this.album = album
        this.folder = folder
        this.genre = genre

        val storageManager = context.getSystemService<StorageManager>()
        val storageVolumes: List<StorageVolume> = storageManager!!.storageVolumes

        for (volume in storageVolumes) {
            absolutePath = "${volume.directory!!.path}/$relativePath/$displayName"
            if (!File(this.absolutePath!!).exists()) {
                if (storageVolumes.last() == volume) {
                    throw IllegalAccessException("This media doesn't exist")
                }
                continue
            }
            this.relativePath = "${volume.directory!!.path.split("/").last()}/$relativePath"
            this.uri = Uri.parse(absolutePath)
            break
        }

        val mediaMetaData: MediaMetadata = MediaMetadata.Builder()
            .setArtist(if (this.artist != null) this.artist!!.title else null)
            .setTitle(this.title)
            .setGenre(if (this.genre != null) this.genre!!.title else null)
            .setAlbumTitle(if (this.album != null) this.album!!.title else null)
            .build()
        this.mediaItem = MediaItem.Builder()
            .setUri(this.uri)
            .setMediaMetadata(mediaMetaData)
            .build()

        if (this.album != null) {
            @Suppress("NAME_SHADOWING")
            val album: Album = this.album as Album
            album.addMusic(music = this)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Music

        if (displayName != other.displayName) return false
        if (artist != other.artist) return false
        if (album != other.album) return false

        return true
    }

    override fun hashCode(): Int {
        var result = displayName.hashCode()
        result = 31 * result + (artist?.hashCode() ?: 0)
        result = 31 * result + (album?.hashCode() ?: 0)
        return result
    }
}