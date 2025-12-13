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

package io.github.antoinepirlot.satunes.database.models.media

import android.net.Uri

/**
 * @author Antoine Pirlot 11/12/2025
 */
class SubsonicMusic(
    id: Long,
    var subsonicId: Long,
    title: String,
    displayName: String,
    absolutePath: String,
    override var durationMs: Long = 0,
    override var size: Int = 0,
    cdTrackNumber: Int? = null,
    addedDateMs: Long,
    folder: Folder,
    artist: Artist,
    album: Album,
    genre: Genre,
    uri: Uri? = null,
) : Music(
    id = id,
    title = title,
    displayName = displayName,
    absolutePath = absolutePath,
    durationMs = durationMs,
    size = size,
    cdTrackNumber = cdTrackNumber,
    addedDateMs = addedDateMs,
    folder = folder,
    artist = artist,
    album = album,
    genre = genre,
    uri = uri,
) {
    override fun isSubsonic(): Boolean = true

    /**
     * Update this [SubsonicMusic] with the [new] [SubsonicMusic] if both [id] are identical
     */
    fun update(new: SubsonicMusic) {
        if (new.id != this.id)
            throw IllegalArgumentException("These musics doesn't have the same id. Old id is: ${this.id} and the new id is: $id")
        this.title = new.title
        this.absolutePath = new.absolutePath
        this.durationMs = new.durationMs
        this.size = new.size
        this.cdTrackNumber = new.cdTrackNumber
        this.addedDate = new.addedDate
        this.folder = new.folder
        this.artist = new.artist
        this.album = new.album
        this.genre = new.genre
        this.uri = new.uri
    }

    /**
     * Save this [SubsonicMusic] in cache.
     */
    fun saveInCache() {
        TODO("Saving in cache is not yet implemented.")
    }

    override fun equals(other: Any?): Boolean {
        return if (this.javaClass == other?.javaClass) this.id == (other as SubsonicMusic).id
        else super.equals(other)
    }
}