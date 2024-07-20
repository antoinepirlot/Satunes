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
    private val playlistsSortedMap: SortedMap<Playlist, Playlist> = sortedMapOf()
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

    fun getMusicSet(): Set<Music> {
        return this.musicMediaItemMap.keys
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

    fun getAlbum(albumId: Long): Album {
        return albumMapById[albumId]!!
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

    fun getFolder(folderId: Long): Folder {
        return folderMapById[folderId]!!
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
        folder.getSubFolderMap().values.forEach {
            this.removeFolder(folder = it)
        }
        rootFolderSortedSet.remove(folder)
    }

    fun getGenre(genreId: Long): Genre {
        return genreMapById[genreId]!!
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
    fun getPlaylist(id: Long): Playlist {
        try {
            return playlistsMapById[id]!!
        } catch (_: NullPointerException) {
            throw PlaylistNotFoundException(id = id)
        }
    }

    fun getPlaylistSet(): Set<Playlist> {
        return this.playlistsSortedMap.keys
    }

    fun addPlaylist(playlist: Playlist) {
        if (!playlistsSortedMap.contains(playlist)) {
            playlistsSortedMap[playlist] = playlist
            playlistsMapUpdated.value = true
        }
        if (!playlistsMapById.contains(playlist.id)) {
            playlistsMapById[playlist.id] = playlist
        }
    }

    fun removePlaylist(playlist: Playlist) {
        if (playlistsSortedMap.contains(playlist)) {
            playlistsSortedMap.remove(playlist)
            playlistsMapUpdated.value = true
        }
        playlistsMapById.remove(playlist.id)
    }
}