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

package earth.mp3player.database.services

import androidx.media3.common.MediaItem
import earth.mp3player.database.models.Album
import earth.mp3player.database.models.Artist
import earth.mp3player.database.models.Folder
import earth.mp3player.database.models.Genre
import earth.mp3player.database.models.Music
import earth.mp3player.database.models.relations.PlaylistWithMusics
import earth.mp3player.database.models.tables.Playlist
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 07/03/2024
 */

object DataManager {
    val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
    private val musicMapById: SortedMap<Long, Music> = sortedMapOf()
    val rootFolderMap: SortedMap<Long, Folder> = sortedMapOf()
    val folderMap: SortedMap<Long, Folder> = sortedMapOf()
    val artistMap: SortedMap<String, Artist> = sortedMapOf()
    private val artistMapById: SortedMap<Long, Artist> = sortedMapOf()
    val albumMap: SortedMap<String, Album> = sortedMapOf()
    private val albumMapById: SortedMap<Long, Album> = sortedMapOf()
    val genreMap: SortedMap<String, Genre> = sortedMapOf()
    private val genreMapById: SortedMap<Long, Genre> = sortedMapOf()
    val playlistWithMusicsMap: SortedMap<String, PlaylistWithMusics> = sortedMapOf() //TODO Remove
    private val playlistWithMusicsMapById: SortedMap<Long, PlaylistWithMusics> = sortedMapOf()

    fun getMusic(musicId: Long): Music {
        return musicMapById[musicId]!!
    }

    fun getMusic(mediaItem: MediaItem): Music {
        return getMusic(musicId = mediaItem.mediaId.toLong())
    }

    fun getMediaItem(music: Music): MediaItem {
        return musicMediaItemSortedMap[music]!!
    }

    fun addMusic(music: Music) {
        musicMediaItemSortedMap.putIfAbsent(music, music.mediaItem)
        musicMapById.putIfAbsent(music.id, music)
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
        artistMap.putIfAbsent(artist.title, artist)
        //You can have multiple same artist's name but different id, but it's the same artist.
        val artistToReturn: Artist = artistMap[artist.title]!!
        artistMapById.putIfAbsent(artistToReturn.id, artist)
        return artistToReturn
    }

    fun getAlbum(albumId: Long): Album {
        return albumMapById[albumId]!!
    }

    fun addAlbum(album: Album) {
        albumMap.putIfAbsent(album.title, album)
        albumMapById.putIfAbsent(album.id, album)
    }

    fun removeAlbum(album: Album) {
        albumMap.remove(album.title)
        albumMapById.remove(album.id)
    }

    fun getFolder(folderId: Long): Folder {
        return folderMap[folderId]!!
    }

    fun addFolder(folder: Folder) {
        folderMap.putIfAbsent(folder.id, folder)
        if (folder.parentFolder == null) {
            rootFolderMap.putIfAbsent(folder.id, folder)
        }
    }

    fun getGenre(genreId: Long): Genre {
        return genreMapById[genreId]!!
    }

    fun getGenre(genreName: String): Genre {
        return genreMap[genreName]!!
    }

    fun addGenre(genre: Genre): Genre {
        genreMap.putIfAbsent(genre.title, genre)
        //You can have multiple same genre's name but different id, but it's the same genre.
        val genreToReturn: Genre = genreMap[genre.title]!!
        genreMapById.putIfAbsent(genreToReturn.id, genre)
        return genreToReturn
    }

    fun getPlaylist(playlistId: Long): PlaylistWithMusics {
        return playlistWithMusicsMapById[playlistId]!!
    }

    fun addPlaylist(playlistWithMusics: PlaylistWithMusics) {
        val playlist: Playlist = playlistWithMusics.playlist
        playlistWithMusicsMap.putIfAbsent(playlist.title, playlistWithMusics)
        playlistWithMusicsMapById.putIfAbsent(playlist.id, playlistWithMusics)
    }

    fun removePlaylist(playlistWithMusics: PlaylistWithMusics) {
        playlistWithMusicsMap.remove(playlistWithMusics.playlist.title)
        playlistWithMusicsMapById.remove(playlistWithMusics.playlist.id)
    }
}