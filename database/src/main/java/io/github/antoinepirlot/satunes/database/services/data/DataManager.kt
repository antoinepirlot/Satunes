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
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.media3.common.MediaItem
import io.github.antoinepirlot.satunes.database.exceptions.DuplicatedAlbumException
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
    val musicMediaItemMap: SortedMap<Music, MediaItem> = sortedMapOf()
    private val musicMapById: MutableMap<Long, Music> = SnapshotStateMap()
    val musicMediaItemSortedMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    val rootFolderMap: MutableMap<Long, Folder> = mutableMapOf()
    val rootFolderSet: SortedSet<Folder> = sortedSetOf()
    private val folderMap: MutableMap<Long, Folder> = mutableMapOf()
    val folderSortedList: SortedSet<Folder> = sortedSetOf()

    private val artistMapById: MutableMap<Long, Artist> = mutableMapOf()
    val artistMap: SortedMap<String, Artist> = sortedMapOf(comparator = StringComparator)
    val artistMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    private val albumMapById: MutableMap<Long, Album> = mutableMapOf()
    val albumSet: SortedSet<Album> = sortedSetOf()
    val albumSetUpdated: MutableState<Boolean> = mutableStateOf(false)

    private val genreMapById: MutableMap<Long, Genre> = mutableMapOf()
    val genreMap: SortedMap<String, Genre> = sortedMapOf(comparator = StringComparator)
    val genreMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    private val playlistsMapById: MutableMap<Long, Playlist> = mutableMapOf()
    val playlistsMap: SortedMap<String, Playlist> =
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

    fun addMusic(music: Music) {
        if (!musicMapById.contains(music.id)) {
            musicMediaItemMap[music] = music.mediaItem
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
        if (!albumMapById.contains(album.id)) {
            albumSet.add(album)
            albumMapById[album.id] = album
            albumSetUpdated.value = true
        }
    }

    fun removeAlbum(album: Album) {
        albumSet.remove(album)
        albumMapById.remove(album.id)
        albumSetUpdated.value = true
    }

    fun getFolder(folderId: Long): Folder {
        return folderMap[folderId]!!
    }

    fun addFolder(folder: Folder) {
        if (!folderMap.contains(folder.id)) {
            folderMap[folder.id] = folder
        }
        if (folder.parentFolder == null && !rootFolderMap.contains(folder.id)) {
            rootFolderMap[folder.id] = folder
            rootFolderSet.add(element = folder)
        }
    }

    fun removeFolder(folder: Folder) {
        if (folderMap.contains(folder.id)) {
            folderMap.remove(folder.id)
        }
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

    @Throws(PlaylistNotFoundException::class)
    fun getPlaylist(id: Long): Playlist {
        try {
            return playlistsMapById[id]!!
        } catch (_: NullPointerException) {
            throw PlaylistNotFoundException(id = id)
        }
    }

    @Throws(NullPointerException::class)
    fun getPlaylist(title: String): Playlist {
        return playlistsMap[title]!!
    }

    fun addPlaylist(playlist: Playlist) {
        val playlistDB: Playlist = playlist
        if (!playlistsMap.contains(playlistDB.title)) {
            playlistsMap[playlistDB.title] = playlist
            playlistsMapUpdated.value = true
        }
        if (!playlistsMapById.contains(playlistDB.id)) {
            playlistsMapById[playlistDB.id] = playlist
        }
    }

    fun removePlaylist(playlist: Playlist) {
        if (playlistsMap.contains(playlist.title)) {
            playlistsMap.remove(playlist.title)
            playlistsMapUpdated.value = true
        }
        playlistsMapById.remove(playlist.id)
    }

    fun updatePlaylist(oldTitle: String, playlist: Playlist) {
        playlistsMap.remove(oldTitle)
        playlistsMap[playlist.title] = playlist
        playlistsMapUpdated.value = true
    }
}