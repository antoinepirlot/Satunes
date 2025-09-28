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

package io.github.antoinepirlot.satunes.database.services.data

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
import java.util.SortedMap
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 07/03/2024
 */

object DataManager {
    // All public map and sortedmap has bool state to recompose as Map are not supported for recomposition
    private var subsonicFolder: Folder? = null
    private val musicSortedSet: SortedSet<Music> = sortedSetOf()
    private val musicMapById: MutableMap<Long, Music> = mutableMapOf()
    private val musicMapByAbsolutePath: MutableMap<String, Music> = mutableMapOf()
    private val subsonicMusicSortedMap: SortedMap<String, Music> = sortedMapOf()

    private val rootFolderSortedSet: SortedSet<Folder> = sortedSetOf()
    private val folderMapById: MutableMap<Long, Folder> = mutableMapOf()
    private val folderSortedSet: SortedSet<Folder> = sortedSetOf()
    private val subsonicFoldersSortedMap: SortedMap<String, Folder> = sortedMapOf()

    private val artistMapById: MutableMap<Long, Artist> = mutableMapOf()
    private val artistMap: SortedMap<Artist, Artist> = sortedMapOf()
    private val subsonicArtists: SortedMap<String, Artist> = sortedMapOf()

    private val albumMapById: MutableMap<Long, Album> = mutableMapOf()

    // Used to know if Album is already in set. This avoid Log(N) process
    private val albumSortedMap: SortedMap<Album, Album> = sortedMapOf()
    private val subsonicAlbumsSortedSet: SortedMap<String, Album> = sortedMapOf()

    private val genreMapById: MutableMap<Long, Genre> = mutableMapOf()
    private val genreMap: SortedMap<String, Genre> = sortedMapOf()

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
    
    fun getSubsonicMusic(subsonicId: String): Music? = this.subsonicMusicSortedMap[subsonicId]

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

    fun getRootSubsonicFolders(): Set<Folder> {
        return this.subsonicFolder?.getSubFolderSet()?: setOf()
    }

    fun getFolderSet(): Set<Folder> {
        return this.folderSortedSet
    }

    fun getArtist(id: Long): Artist? {
        return artistMapById[id]
    }

    fun getArtist(subsonicId: String): Artist? {
        return this.subsonicArtists[subsonicId]
    }
    fun getArtistSet(): Set<Artist> {
        return this.artistMap.keys
    }

    fun hasSubsonicArtists(): Boolean = this.subsonicArtists.isNotEmpty()

    fun getSubsonicArtistSet(): Collection<Artist> {
        return this.subsonicArtists.values
    }

    fun getSubsonicArtist(id: String): Artist? {
        return this.subsonicArtists[id]
    }

    fun addArtist(artist: Artist): Artist {
        if (artistMap[artist] == null) {
            artistMap[artist] = artist
            artistMapById[artist.id] = artist
            if(artist.isSubsonic()) this.subsonicArtists[artist.subsonicId] = artist
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

    @Synchronized
    fun getSubsonicAlbumsSet(): Collection<Album> {
        return this.subsonicAlbumsSortedSet.values
    }

    fun hasSubsonicAlbums(): Boolean {
        return this.subsonicAlbumsSortedSet.isNotEmpty()
    }
    
    fun getAlbum(subsonicId: String): Album? = this.subsonicAlbumsSortedSet[subsonicId]

    fun addAlbum(album: Album): Album {
        if (this.albumSortedMap[album] == null) {
            this.albumSortedMap[album] = album
            this.albumMapById[album.id] = album
            if(album.isSubsonic()) this.subsonicAlbumsSortedSet[album.subsonicId] = album
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

    fun getFolder(subsonicId: String): Folder? = this.subsonicFoldersSortedMap[subsonicId]

    /**
     * Returns the subsonic folder if it has been created, null otherwise.
     */
    fun getSubsonicRootFolder(): Folder? {
        return this.subsonicFolder
    }

    fun hasSubsonicFolders(): Boolean {
        return this.subsonicFolder != null
    }

    private fun addSubsonicFolder(subsonicFolder: Folder) {
        if(!subsonicFolder.isSubsonic()) throw IllegalArgumentException("subsonic folder is not a subsonic one.")
        if(this.subsonicFolder == null) this.subsonicFolder = subsonicFolder
        this.subsonicFoldersSortedMap[subsonicFolder.subsonicId] = subsonicFolder
    }

    fun addFolder(folder: Folder) {
        if (!folderSortedSet.contains(folder)) {
            this.folderMapById[folder.id] = folder
            this.folderSortedSet.add(element = folder)
            if(folder.isSubsonic()) this.addSubsonicFolder(subsonicFolder = folder)
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

    fun getGenre(title: String): Genre? = this.genreMap[title]

    fun getGenreSet(): Collection<Genre> {
        return this.genreMap.values
    }

    fun addGenre(genre: Genre): Genre {
        if (!genreMap.contains(key = genre.title)) {
            genreMap[genre.title] = genre
            genreMapById[genre.id] = genre
            return genre
        }
        //You can have multiple same genre's name but different id, but it's the same genre.
        return genreMap[genre.title]!!
    }

    fun removeGenre(genre: Genre) {
        genreMap.remove(genre.title)
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

    fun remove(music: Music) {
        this.musicMapById.remove(music.id)
        this.musicMapByAbsolutePath.remove(music.absolutePath)
        this.musicSortedSet.remove(music)
    }
}