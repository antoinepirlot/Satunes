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

package io.github.antoinepirlot.satunes.database.models.media

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import io.github.antoinepirlot.jetpack_libs.components.R
import io.github.antoinepirlot.satunes.database.models.comparators.MusicInAlbumComparator
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 27/03/2024
 */

open class Album(
    id: Long? = null,
    title: String,
    protected var coverArtId: String? = null,
    var artist: Artist,
    var isCompilation: Boolean = false,
    year: Int? = null
) : MediaImpl(
    id = id ?: nextId,
    title = title
) {

    companion object {
        var nextId: Long = 1
    }

    override val musicSortedSet: SortedSet<Music> = sortedSetOf(MusicInAlbumComparator)
    val year: Int? = if (year != null && year < 1) null else year
    init {
        nextId++
        artist.addAlbum(album = this)
    }

    fun updateArtist(artist: Artist) {
        this.artist = artist
    }

    /**
     * Transform this [Album] to [SubsonicAlbum].
     * After that, this [Album] can't no more be used
     */
    open fun toSubsonicAlbum(album: SubsonicAlbum): SubsonicAlbum {
        val newAlbum: SubsonicAlbum = SubsonicAlbum(
            id = this.id,
            subsonicId = album.subsonicId,
            title = this.title,
            artist = this.artist,
            isCompilation = this.isCompilation,
            year = this.year,
        )
        for (music: Music in this.musicSortedSet) {
            music.updateAlbum(album = newAlbum)
            music.artist.updateAlbum(album = newAlbum)
        }
        DataManager.addAlbum(album = newAlbum)
        return newAlbum
    }

    internal fun getEmptyAlbumArtwork(context: Context): Bitmap = AppCompatResources.getDrawable(
        context,
        R.mipmap.empty_album_artwork_foreground
    )!!.toBitmap()

    override fun isAlbum(): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Album

        if (title != other.title) return false
        if (artist != other.artist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (artist.hashCode())
        return result
    }

    override fun compareTo(other: Media): Int {
        var compared: Int = super.compareTo(other)
        if (compared == 0 && other.isAlbum()) {
            compared = this.artist.compareTo((other as Album).artist)
        }
        return compared
    }

    override fun toString(): String {
        return "$title - $artist"
    }
}