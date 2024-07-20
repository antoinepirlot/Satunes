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

package io.github.antoinepirlot.satunes.database.services.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import io.github.antoinepirlot.satunes.database.exceptions.MusicNotFoundException
import io.github.antoinepirlot.satunes.database.exceptions.PlaylistNotFoundException
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.models.StringComparator
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import java.util.SortedMap
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 07/03/2024
 */

object DataManager {
    // All public map and sortedmap has bool state to recompose as Map are not supported for recomposition
    private val musicMediaItemMap: SortedMap<Music, MediaItem> = sortedMapOf()
    private val musicMapById: MutableMap<Long, Music> = mutableMapOf()

    private val rootFolderMap: MutableMap<String, Folder> = mutableMapOf()
    private val rootFolderSortedSet: SortedSet<Folder> = sortedSetOf()
    private val folderMap: MutableMap<String, Folder> = mutableMapOf()
    private val folderMapById: MutableMap<Long, Folder> = mutableMapOf()
    private val folderSortedSet: SortedSet<Folder> = sortedSetOf()

    private val artistMapById: MutableMap<Long, Artist> = mutableMapOf()
    private val artistMap: SortedMap<String, Artist> = sortedMapOf(comparator = StringComparator)

    private val albumMapById: MutableMap<Long, Album> = mutableMapOf()

    // Used to know if Album is already in set. This avoid Log(N) process
    private val albumSortedMap: SortedMap<Album, Album> = sortedMapOf()

    private val genreMapById: MutableMap<Long, Genre> = mutableMapOf()
    private val genreMap: SortedMap<String, Genre> = sortedMapOf(comparator = StringComparator)

    private val playlistsMapById: MutableMap<Long, Playlist> = mutableMapOf()
    private val playlistsSortedMap: SortedMap<String, Playlist> =
        sortedMapOf(comparator = StringComparator)
    val playlistsMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    private val logger = SatunesLogger(name = this::class.java.name)


    fun getMusic(musicId: Long): Music {
        try {
            return musicMapById[musicId]!!
        } catch (_: NullPointerException) {
            //That means the music is not more present in the phone storage
            //Happens when the database is loaded with old informations.
            throw MusicNotFoundException(id = musicId)
        }
    }

    fun getMusic(mediaItem: MediaItem): Music {
        return getMusic(musicId = mediaItem.mediaId.toLong())
    }

    fun getMediaItem(music: Music): MediaItem {
        return musicMediaItemMap[music]!!
    }

    fun getMusicMap(): Map<Music, MediaItem> {
        return this.musicMediaItemMap
    }

    fun addMusic(music: Music): Music {
        if (this.musicMapById[music.id] == null) {
            musicMediaItemMap[music] = music.mediaItem
            musicMapById[music.id] = music
        }
        return getMusic(musicId = music.id)
    }

    fun getRootFolderSet(): Set<Folder> {
        return this.rootFolderSortedSet
    }

    fun getFolderSet(): Set<Folder> {
        return this.folderSortedSet
    }

    fun getArtist(artistId: Long): Artist {
        return artistMapById[artistId]!!
    }

    fun getArtist(artistName: String): Artist {
        return artistMap[artistName]!!
    }

    fun getArtistMap(): Map<String, Artist> {
        return this.artistMap
    }

    fun addArtist(artist: Artist): Artist {
        if (artistMap[artist.title] == null) {
            artistMap[artist.title] = artist
            artistMapById[artist.id] = artist
        }

        return getArtist(artistName = artist.title)
    }

    fun removeArtist(artist: Artist) {
        if (artistMap.contains(artist.title)) {
            artistMap.remove(artist.title)
        }
        artistMapById.remove(artist.id)
    }

    fun getAlbum(albumId: Long): Album {
        return albumMapById[albumId]!!
    }

    fun getAlbum(albumName: String): Album {
        return albumSortedMap.keys.first { it.title == albumName }
    }

    fun getAlbumMap(): Map<Album, Album> {
        return this.albumSortedMap
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

    fun getFolder(folderId: Long): Folder {
        return folderMapById[folderId]!!
    }

    fun addFolder(folder: Folder): Folder {
        if (folderMap[folder.absolutePath] == null) {
            this.folderMap[folder.absolutePath] = folder
            this.folderMapById[folder.id] = folder
            this.folderSortedSet.add(element = folder)
            if (folder.parentFolder == null) {
                this.rootFolderMap[folder.absolutePath] = folder
                this.rootFolderSortedSet.add(element = folder)
            }
        }
        return this.folderMap[folder.absolutePath]!!
    }

    /**
     * Remove folder and its subfolder from data
     */
    fun removeFolder(folder: Folder) {
        this.folderMap.remove(key = folder.absolutePath)
        folder.getSubFolderMap().values.forEach {
            this.removeFolder(folder = it)
        }
        rootFolderMap.remove(key = folder.absolutePath)
    }

    fun getGenre(genreId: Long): Genre {
        return genreMapById[genreId]!!
    }

    fun getGenre(genreName: String): Genre {
        return genreMap[genreName]!!
    }

    fun getGenreMap(): Map<String, Genre> {
        return this.genreMap
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
    fun getPlaylist(id: Long): Playlist {
        try {
            return playlistsMapById[id]!!
        } catch (_: NullPointerException) {
            throw PlaylistNotFoundException(id = id)
        }
    }

    fun getPlaylistMap(): Map<String, Playlist> {
        return this.playlistsSortedMap
    }

    @Throws(NullPointerException::class)
    fun getPlaylist(title: String): Playlist {
        return playlistsSortedMap[title]!!
    }

    fun addPlaylist(playlist: Playlist) {
        val playlistDB: Playlist = playlist
        if (!playlistsSortedMap.contains(playlistDB.title)) {
            playlistsSortedMap[playlistDB.title] = playlist
            playlistsMapUpdated.value = true
        }
        if (!playlistsMapById.contains(playlistDB.id)) {
            playlistsMapById[playlistDB.id] = playlist
        }
    }

    fun removePlaylist(playlist: Playlist) {
        if (playlistsSortedMap.contains(playlist.title)) {
            playlistsSortedMap.remove(playlist.title)
            playlistsMapUpdated.value = true
        }
        playlistsMapById.remove(playlist.id)
    }

    fun updatePlaylist(oldTitle: String, playlist: Playlist) {
        playlistsSortedMap.remove(oldTitle)
        playlistsSortedMap[playlist.title] = playlist
        playlistsMapUpdated.value = true
    }
}