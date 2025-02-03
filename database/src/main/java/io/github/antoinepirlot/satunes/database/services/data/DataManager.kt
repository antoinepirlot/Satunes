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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.services.data

import android.content.ContentResolver
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.antoinepirlot.satunes.database.exceptions.MusicNotFoundException
import io.github.antoinepirlot.satunes.database.exceptions.PlaylistNotFoundException
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.id3.ID3v1Tag
import java.io.File
import java.io.InputStream
import java.util.SortedMap
import java.util.SortedSet


/**
 * @author Antoine Pirlot on 07/03/2024
 */

object DataManager {
    // All public map and sortedmap has bool state to recompose as Map are not supported for recomposition
    private val musicSortedSet: SortedSet<Music> = sortedSetOf()
    private val musicMapById: MutableMap<Long, Music> = mutableMapOf()
    private val musicMapByAbsolutePath: MutableMap<String, Music> = mutableMapOf()

    private val rootFolderSortedSet: SortedSet<Folder> = sortedSetOf()
    private val folderMapById: MutableMap<Long, Folder> = mutableMapOf()
    private val folderSortedSet: SortedSet<Folder> = sortedSetOf()

    private val artistMapById: MutableMap<Long, Artist> = mutableMapOf()
    private val artistMap: SortedMap<Artist, Artist> = sortedMapOf()

    private val albumMapById: MutableMap<Long, Album> = mutableMapOf()

    // Used to know if Album is already in set. This avoid Log(N) process
    private val albumSortedMap: SortedMap<Album, Album> = sortedMapOf()

    private val genreMapById: MutableMap<Long, Genre> = mutableMapOf()
    private val genreMap: SortedMap<Genre, Genre> = sortedMapOf()

    private val playlistsMapById: MutableMap<Long, Playlist> = mutableMapOf()
    private val playlistsMapByTitle: MutableMap<String, Playlist> = mutableMapOf()
    private val playlistsSortedMap: SortedMap<Playlist, Playlist> = sortedMapOf()
    val playlistsMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    fun getMusic(id: Long): Music {
        try {
            return musicMapById[id]!!
        } catch (_: NullPointerException) {
            //That means the music is not more present in the phone storage
            //Happens when the database is loaded with old informations.
            throw MusicNotFoundException(id = id)
        }
    }

    fun getMusic(absolutePath: String): Music {
        return musicMapByAbsolutePath[absolutePath]!!
    }

    fun getMusicSet(): Set<Music> {
        return this.musicSortedSet
    }

    fun addMusic(music: Music): Music {
        if (this.musicMapById[music.id] == null) {
            this.musicSortedSet.add(element = music)
            this.musicMapById[music.id] = music
            this.musicMapByAbsolutePath[music.absolutePath] = music
        }
        return getMusic(id = music.id)
    }

    fun getRootFolderSet(): Set<Folder> {
        return this.rootFolderSortedSet
    }

    fun getFolderSet(): Set<Folder> {
        return this.folderSortedSet
    }

    fun getArtist(id: Long): Artist? {
        return artistMapById[id]
    }

    fun getArtistSet(): Set<Artist> {
        return this.artistMap.keys
    }

    fun addArtist(artist: Artist): Artist {
        if (artistMap[artist] == null) {
            artistMap[artist] = artist
            artistMapById[artist.id] = artist
        }

        return this.artistMap[artist]!!
    }

    fun removeArtist(artist: Artist) {
        if (artistMap.contains(artist)) {
            artistMap.remove(artist)
        }
        artistMapById.remove(artist.id)
    }

    fun getAlbum(id: Long): Album? {
        return albumMapById[id]
    }

    fun getAlbumSet(): Set<Album> {
        return this.albumSortedMap.keys
    }

    fun addAlbum(album: Album): Album {
        if (this.albumSortedMap[album] == null) {
            this.albumSortedMap[album] = album
            this.albumMapById[album.id] = album
        }

        return this.albumSortedMap[album]!!
    }

    fun removeAlbum(album: Album) {
        albumSortedMap.remove(key = album)
        albumMapById.remove(album.id)
    }

    fun getFolder(id: Long): Folder? {
        return folderMapById[id]!!
    }

    fun addFolder(folder: Folder) {
        if (!folderSortedSet.contains(folder)) {
            this.folderMapById[folder.id] = folder
            this.folderSortedSet.add(element = folder)
            if (folder.parentFolder == null) {
                this.rootFolderSortedSet.add(element = folder)
            }
        }
    }

    /**
     * Remove folder and its subfolder from data
     */
    fun removeFolder(folder: Folder) {
        folder.getSubFolderSet().forEach {
            this.removeFolder(folder = it)
        }
        rootFolderSortedSet.remove(folder)
    }

    fun getGenre(id: Long): Genre? {
        return genreMapById[id]
    }

    fun getGenreSet(): Set<Genre> {
        return this.genreMap.keys
    }

    fun addGenre(genre: Genre): Genre {
        if (!genreMap.contains(key = genre)) {
            genreMap[genre] = genre
            genreMapById[genre.id] = genre
            return genre
        }
        //You can have multiple same genre's name but different id, but it's the same genre.
        return genreMap[genre]!!
    }

    fun removeGenre(genre: Genre) {
        genreMap.remove(genre)
        genreMapById.remove(genre.id)
    }

    @Throws(PlaylistNotFoundException::class)
    fun getPlaylist(id: Long): Playlist? = playlistsMapById[id]

    fun getPlaylist(title: String): Playlist? = playlistsMapByTitle[title]

    fun getPlaylistSet(): Set<Playlist> {
        return this.playlistsSortedMap.keys
    }

    fun addPlaylist(playlist: Playlist): Playlist {
        if (!playlistsSortedMap.contains(playlist)) {
            playlistsSortedMap[playlist] = playlist
            playlistsMapById[playlist.id] = playlist
            playlistsMapByTitle[playlist.title] = playlist
            playlistsMapUpdated.value = true
        }
        return this.playlistsMapById[playlist.id]!!
    }

    fun removePlaylist(playlist: Playlist) {
        if (playlistsSortedMap.contains(playlist)) {
            playlistsSortedMap.remove(playlist)
            playlistsMapById.remove(playlist.id)
            playlistsMapByTitle.remove(playlist.title)
            playlistsMapUpdated.value = true
        }
    }

    internal fun resetAllData() {
        musicSortedSet.clear()
        musicMapById.clear()
        musicMapByAbsolutePath.clear()
        rootFolderSortedSet.clear()
        folderMapById.clear()
        folderSortedSet.clear()
        artistMapById.clear()
        artistMap.clear()
        albumMapById.clear()
        albumSortedMap.clear()
        genreMapById.clear()
        genreMap.clear()
        playlistsMapById.clear()
        playlistsMapByTitle.clear()
        playlistsSortedMap.clear()
        playlistsMapUpdated.value = true
    }

    /**
     * Updates the matching music with new modifiable values and apply these modifications
     * in audio file.
     *
     * @param updatedMusic a [Music] containing the new values to apply to the current music.
     *
     * @throws IllegalArgumentException if the [updatedMusic] is not valid.
     */
    fun updateMusic(context: Context, updatedMusic: Music, onError: (() -> Unit)?) {
        val resolver: ContentResolver = context.contentResolver
        val outputFile = File(updatedMusic.absolutePath)
        resolver.openInputStream(updatedMusic.uri)
            ?.use { stream -> outputFile.copyInputStreamToFile(stream, updatedMusic) }
//        val mp3File = Mp3File(updatedMusic.absolutePath)
//        if(mp3File.hasId3v2Tag()) {
//            mp3File.id3v2Tag.title = updatedMusic.title
//        }
//        context.contentResolver.openOutputStream(updatedMusic.uri, "w")?.use { stream ->
//            stream.write(outputFile.readBytes())
//        }
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream?, updatedMusic: Music) {
        this.outputStream().use { fileOut ->
            inputStream?.copyTo(fileOut)
            val mp3File: MP3File = AudioFileIO.read(this) as MP3File
            val tags: ID3v1Tag = mp3File.iD3v1Tag
            println(tags.title)
            tags.setField(FieldKey.TITLE, updatedMusic.title)
            mp3File.commit()
            fileOut.close()
        }
    }
}