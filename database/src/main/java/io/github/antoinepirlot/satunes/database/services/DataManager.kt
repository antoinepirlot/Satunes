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

package io.github.antoinepirlot.satunes.database.services

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import io.github.antoinepirlot.satunes.database.exceptions.DuplicatedAlbumException
import io.github.antoinepirlot.satunes.database.exceptions.MusicNotFoundException
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.StringComparator
import io.github.antoinepirlot.satunes.database.models.relations.PlaylistWithMusics
import io.github.antoinepirlot.satunes.database.models.tables.Playlist
import java.util.SortedMap
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 07/03/2024
 */

object DataManager {
    // All public map and sortedmap has bool state to recompose as Map are not supported for recomposition
    val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
    val musicMediaItemSortedMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    private val musicMapById: MutableMap<Long, Music> = mutableMapOf()

    val rootFolderMap: SortedMap<Long, Folder> = sortedMapOf()
    val rootFolderMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    val folderMap: SortedMap<Long, Folder> = sortedMapOf()
    val folderMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    val artistMap: SortedMap<String, Artist> = sortedMapOf(comparator = StringComparator)
    val artistMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    private val artistMapById: MutableMap<Long, Artist> = mutableMapOf()

    val albumSet: SortedSet<Album> = sortedSetOf()

    private val albumMapById: MutableMap<Long, Album> = mutableMapOf()

    val genreMap: SortedMap<String, Genre> = sortedMapOf(comparator = StringComparator)
    val genreMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    private val genreMapById: MutableMap<Long, Genre> = mutableMapOf()

    val playlistWithMusicsMap: SortedMap<String, PlaylistWithMusics> =
        sortedMapOf(comparator = StringComparator)
    val playlistWithMusicsMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    private val playlistWithMusicsMapById: MutableMap<Long, PlaylistWithMusics> = mutableMapOf()

    fun getMusic(musicId: Long): Music {
        try {
            return musicMapById[musicId]!!
        } catch (_: NullPointerException) {
            //That means the music is not more present in the phone storage
            //Happens when the database is loaded with old informations.
            throw MusicNotFoundException(musicId = musicId)
        }
    }

    fun getMusic(mediaItem: MediaItem): Music {
        return getMusic(musicId = mediaItem.mediaId.toLong())
    }

    fun getMediaItem(music: Music): MediaItem {
        return musicMediaItemSortedMap[music]!!
    }

    fun addMusic(music: Music) {
        if (!musicMediaItemSortedMap.contains(music)) {
            musicMediaItemSortedMap[music] = music.mediaItem
            musicMediaItemSortedMapUpdated.value = true
        }
        if (!musicMapById.contains(music.id)) {
            musicMapById[music.id] = music
        }
    }

    fun getArtist(artist: Artist): Artist {
        return artistMapById[artist.id]!!
    }

    fun getArtist(artistId: Long): Artist {
        return artistMapById[artistId]!!
    }

    fun getArtist(artistName: String): Artist {
        return artistMap[artistName]!!
    }

    fun addArtist(artist: Artist): Artist {
        if (!artistMap.contains(artist.title)) {
            artistMap[artist.title] = artist
            artistMapUpdated.value = true
        }
        //You can have multiple same artist's name but different id, but it's the same artist.
        val artistToReturn: Artist = artistMap[artist.title]!!
        if (!artistMapById.containsKey(artistToReturn.id)) {
            artistMapById[artistToReturn.id] = artist
        }
        return artistToReturn
    }

    fun removeArtist(artist: Artist) {
        if (artistMap.contains(artist.title)) {
            artistMap.remove(artist.title)
            artistMapUpdated.value = true
        }
        artistMapById.remove(artist.id)
    }

    fun getAlbum(albumId: Long): Album {
        return albumMapById[albumId]!!
    }

    fun getAlbum(albumName: String): Album {
        return albumSet.first { it.title == albumName }
    }

    fun addAlbum(album: Album) {
        if (albumSet.contains(album)) {
            val existingAlbum: Album = albumMapById.values.first { it == album }
            throw DuplicatedAlbumException(existingAlbum = existingAlbum)
        }
        albumSet.add(album)
        if (!albumMapById.contains(album.id)) {
            albumMapById[album.id] = album
        }
    }

    fun removeAlbum(album: Album) {
        albumSet.remove(album)
        albumMapById.remove(album.id)
    }

    fun getFolder(folderId: Long): Folder {
        return folderMap[folderId]!!
    }

    fun addFolder(folder: Folder) {
        if (!folderMap.contains(folder.id)) {
            folderMap[folder.id] = folder
            folderMapUpdated.value = true
        }
        if (folder.parentFolder == null && !rootFolderMap.contains(folder.id)) {
            rootFolderMap[folder.id] = folder
            rootFolderMapUpdated.value = true
        }
    }

    fun removeFolder(folder: Folder) {
        if (folderMap.contains(folder.id)) {
            folderMap.remove(folder.id)
            folderMapUpdated.value = true
        }
        rootFolderMap.remove(folder.id)
    }

    fun getGenre(genreId: Long): Genre {
        return genreMapById[genreId]!!
    }

    fun getGenre(genreName: String): Genre {
        return genreMap[genreName]!!
    }

    fun addGenre(genre: Genre): Genre {
        if (!genreMap.contains(genre.title)) {
            genreMap[genre.title] = genre
            genreMapUpdated.value = true
        }
        //You can have multiple same genre's name but different id, but it's the same genre.
        val genreToReturn: Genre = genreMap[genre.title]!!
        if (!genreMapById.contains(genreToReturn.id)) {
            genreMapById[genreToReturn.id] = genre
        }
        return genreToReturn
    }

    fun removeGenre(genre: Genre) {
        if (genreMap.contains(genre.title)) {
            genreMap.remove(genre.title)
            genreMapUpdated.value = true
        }
        genreMapById.remove(genre.id)
    }

    fun getPlaylist(playlistId: Long): PlaylistWithMusics {
        return playlistWithMusicsMapById[playlistId]!!
    }

    fun addPlaylist(playlistWithMusics: PlaylistWithMusics) {
        val playlist: Playlist = playlistWithMusics.playlist
        if (!playlistWithMusicsMap.contains(playlist.title)) {
            playlistWithMusicsMap[playlist.title] = playlistWithMusics
            playlistWithMusicsMapUpdated.value = true
        }
        if (!playlistWithMusicsMapById.contains(playlist.id)) {
            playlistWithMusicsMapById[playlist.id] = playlistWithMusics
        }
    }

    fun removePlaylist(playlistWithMusics: PlaylistWithMusics) {
        val playlist: Playlist = playlistWithMusics.playlist
        if (playlistWithMusicsMap.contains(playlist.title)) {
            playlistWithMusicsMap.remove(playlist.title)
            playlistWithMusicsMapUpdated.value = true
        }
        playlistWithMusicsMapById.remove(playlist.id)
    }
}