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

import io.github.antoinepirlot.satunes.database.exceptions.media.CloudMusicNotFoundException
import io.github.antoinepirlot.satunes.database.exceptions.media.LocalMusicNotFoundException
import io.github.antoinepirlot.satunes.database.exceptions.media.LocalPlaylistNotFoundException
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.BackFolder
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.models.media.RootFolder
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicFolder
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicGenre
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMusic
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import io.github.antoinepirlot.satunes.database.services.data.DataManager._subsonicMusicsMapById
import java.util.SortedMap
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 07/03/2024
 */

object DataManager {
    // All public map and sortedmap has bool state to recompose as Map are not supported for recomposition

    private var _subsonicRootFolder: SubsonicFolder =
        SubsonicFolder(subsonicId = "0", title = SubsonicFolder.SUBSONIC_FOLDER_TITLE)

    @get:Synchronized
    private val _musicSortedSet: SortedSet<Music> = sortedSetOf()

    @get:Synchronized
    private val _musicsMapById: MutableMap<Long, Music> = mutableMapOf()

    @get:Synchronized
    private val _musicMapByAbsolutePath: MutableMap<String, Music> = mutableMapOf()

    @get:Synchronized
    private val _subsonicMusicsMapById: MutableMap<String, SubsonicMusic> = mutableMapOf()

    @get:Synchronized
    private val _subsonicMusicsSortedMap: SortedMap<SubsonicMusic, SubsonicMusic> = sortedMapOf()

    /**
     * [Collection] of [SubsonicMusic] got by the random music query
     * This [Collection] contains a maximum of 500 [SubsonicMusic] due to the API's limitation
     */
    @get:Synchronized
    private val _subsonicRandomMusicsSortedMap: SortedMap<SubsonicMusic, SubsonicMusic> =
        sortedMapOf()

    private var _rootFolder: RootFolder = RootFolder()

    /**
     * Back folder to back in [Folder]'s tree. Always generate a new one.
     */
    private val _backFolder: BackFolder
        get() = BackFolder()

    @get:Synchronized
    private val _folderMapById: MutableMap<Long, Folder> = mutableMapOf()

    @get:Synchronized
    private val _folderSortedSet: SortedSet<Folder> = sortedSetOf()

    @get:Synchronized
    private val _subsonicFoldersMapById: MutableMap<String, SubsonicFolder> = mutableMapOf()

    @get:Synchronized
    private val _artistsMapById: MutableMap<Long, Artist> = mutableMapOf()

    @get:Synchronized
    private val _artistMap: SortedMap<Artist, Artist> = sortedMapOf()

    @get:Synchronized
    private val _subsonicArtistsMapById: MutableMap<String, SubsonicArtist> = mutableMapOf()

    @get:Synchronized
    private val _albumsMapById: MutableMap<Long, Album> = mutableMapOf()

    @get:Synchronized
    private val _albumSortedMap: SortedMap<Album, Album> = sortedMapOf()

    @get:Synchronized
    private val _subsonicAlbumsMapById: MutableMap<String, SubsonicAlbum> = mutableMapOf()

    @get:Synchronized
    private val _genresMapById: MutableMap<Long, Genre> = mutableMapOf()

    @get:Synchronized
    private val _subsonicGenreMapById: MutableMap<String, SubsonicGenre> = mutableMapOf()

    @get:Synchronized
    private val _genreMap: SortedMap<String, Genre> = sortedMapOf()

    @get:Synchronized
    private val _playlistsMapById: MutableMap<Long, Playlist> = mutableMapOf()

    @get:Synchronized
    private val _subsonicPlaylistMapById: MutableMap<String, SubsonicPlaylist> = mutableMapOf()

    @get:Synchronized
    private val _playlistsMapByTitle: MutableMap<String, Playlist> = mutableMapOf()

    @get:Synchronized
    private val _playlistsSortedMap: SortedMap<Playlist, Playlist> = sortedMapOf()

    @Synchronized
    fun getMusic(id: Long): Music {
        return try {
            this._musicsMapById[id]!!
        } catch (_: NullPointerException) {
            //That means the music is not more present in the phone storage
            //Happens when the database is loaded with old information.
            throw LocalMusicNotFoundException(id = id)
        }
    }

    fun getMusic(id: String): SubsonicMusic {
        return if (this._subsonicMusicsMapById.contains(key = id))
            this._subsonicMusicsMapById[id]!!
        else
        //That means the music is not more present in the phone storage
        //Happens when the database is loaded with old information.
            throw CloudMusicNotFoundException(id = id)
    }

    fun getMusicByPath(absolutePath: String): Music {
        return _musicMapByAbsolutePath[absolutePath]!!
    }

    fun getSubsonicMusic(id: String): SubsonicMusic? = this._subsonicMusicsMapById[id]

    fun getMusicSet(): Set<Music> {
        return this._musicSortedSet
    }

    fun getSubsonicMusicsCollection(): Collection<SubsonicMusic> {
        return this._subsonicMusicsSortedMap.keys.toSet()
    }

    fun getSubsonicRandomMusicsCollection(): Collection<SubsonicMusic> {
        return this._subsonicRandomMusicsSortedMap.keys.toSet()
    }

    @Synchronized
    fun addMusic(music: Music): Music {
        if (!this._musicsMapById.contains(key = music.id!!)) {
            this._musicSortedSet.add(element = music)
            this._musicsMapById[music.id!!] = music
            this._musicMapByAbsolutePath[music.absolutePath] = music
        }
        return this._musicsMapById[music.id]!!
    }

    @Synchronized
    fun addMusic(music: SubsonicMusic): SubsonicMusic {
        if (!this._subsonicMusicsMapById.contains(key = music.subsonicId))
            this._subsonicMusicsMapById[music.subsonicId] = music
        return this._subsonicMusicsMapById[music.subsonicId]!!

    }

    /**
     * Add [musics] and add them to random music list
     *
     * @param musics the newly added [Collection] of [SubsonicMusic].
     */
    @Synchronized
    fun addRandomMusic(musics: Collection<SubsonicMusic>) {
        for (music: SubsonicMusic in musics)
            this.addRandomMusic(subsonicMusic = music)
    }

    /**
     * Add [subsonicMusic] to random music list only. Do not stores it in [_subsonicMusicsMapById]
     *
     * @param subsonicMusic the newly added [SubsonicMusic]
     *
     * @return [SubsonicMusic] that has been added.
     */
    @Synchronized
    fun addRandomMusic(subsonicMusic: SubsonicMusic): SubsonicMusic {
        if (!this._subsonicRandomMusicsSortedMap.contains(key = subsonicMusic))
            this._subsonicRandomMusicsSortedMap[subsonicMusic] = subsonicMusic
        else
            this._subsonicRandomMusicsSortedMap[subsonicMusic]!!.update(new = subsonicMusic)
        return this._subsonicRandomMusicsSortedMap[subsonicMusic]!!
    }

    /**
     * Returns the very first folder in chain.
     */
    fun getRootFolder(): RootFolder = this._rootFolder

    fun getBackFolder(): BackFolder = this._backFolder

    fun getRootSubsonicFolders(): Set<Folder> {
        return this._subsonicRootFolder.getSubFolderSet()
    }

    fun getFolderSet(): Set<Folder> {
        return this._folderSortedSet
    }

    fun getArtist(id: Long): Artist? = this._artistsMapById[id]

    fun getArtistSet(): Set<Artist> {
        return this._artistMap.keys
    }

    fun getSubsonicArtistSet(): Collection<Artist> {
        return this._subsonicArtistsMapById.values
    }

    fun getSubsonicArtist(id: String): SubsonicArtist? = this._subsonicArtistsMapById[id]

    fun hasSubsonicArtists(): Boolean = this._subsonicArtistsMapById.isNotEmpty()

    @Synchronized
    fun addArtist(artist: Artist): Artist {
        if (!_artistMap.contains(key = artist)) {
            _artistMap[artist] = artist
            _artistsMapById[artist.id!!] = artist
        }
        return this._artistMap[artist]!!
    }

    @Synchronized
    fun addArtist(artist: SubsonicArtist): SubsonicArtist {
        if (this._artistMap.contains(key = artist)) {
            this.removeArtist(artist = artist)
            this.addArtist(artist = artist as Artist) //TODO check if it will loop or not.
        }
        if (!_subsonicArtistsMapById.contains(key = artist.subsonicId))
            _subsonicArtistsMapById[artist.subsonicId] = artist
        return _subsonicArtistsMapById[artist.subsonicId]!!
    }

    @Synchronized
    fun removeArtist(artist: Artist) {
        if (_artistMap.contains(artist)) {
            _artistMap.remove(key = artist)
            if (artist.isSubsonic()) this._subsonicArtistsMapById.remove(key = (artist as SubsonicArtist).subsonicId)
            else _artistsMapById.remove(key = artist.id)
        }
    }

    fun getAlbum(id: Long): Album? = this._albumsMapById[id]

    fun getSubsonicAlbum(id: String): SubsonicAlbum? = this._subsonicAlbumsMapById[id]

    fun getAlbumSet(): Set<Album> {
        return this._albumSortedMap.keys
    }

    fun getSubsonicAlbumsSet(): Collection<Album> {
        return this._subsonicAlbumsMapById.values
    }

    fun hasSubsonicAlbums(): Boolean {
        return this._subsonicAlbumsMapById.isNotEmpty()
    }

    @Synchronized
    fun addAlbum(album: Album): Album {
        if (!this._albumSortedMap.contains(key = album)) {
            this._albumSortedMap[album] = album
            this._albumsMapById[album.id!!] = album
        }
        return this._albumSortedMap[album]!!
    }

    @Synchronized
    fun addAlbum(album: SubsonicAlbum): SubsonicAlbum {
        if (this._albumSortedMap.contains(key = album)) {
            this.removeAlbum(album = album)
            this.addAlbum(album = album as Album) //TODO check if it will loop or not.
        }
        if (!_subsonicAlbumsMapById.contains(key = album.subsonicId))
            this._subsonicAlbumsMapById[album.subsonicId] = album
        return _subsonicAlbumsMapById[album.subsonicId]!!
    }

    @Synchronized
    fun removeAlbum(album: Album) {
        if (this._albumSortedMap.contains(key = album)) {
            _albumSortedMap.remove(key = album)
            if (album.isSubsonic()) this._subsonicAlbumsMapById.remove(key = (album as SubsonicAlbum).subsonicId)
            else _albumsMapById.remove(key = album.id)
        }
    }

    fun getFolder(id: Long): Folder? = this._folderMapById[id]

    fun getSubsonicFolder(id: String) = this._subsonicFoldersMapById[id]

    /**
     * Returns the subsonic folder if it has been created, null otherwise.
     */
    fun getSubsonicRootFolder(): Folder {
        return this._subsonicRootFolder
    }

    @Synchronized
    fun addFolder(folder: Folder) {
        if (!_folderSortedSet.contains(folder)) {
            this._folderMapById[folder.id!!] = folder
            this._folderSortedSet.add(element = folder)
        }
    }

    @Synchronized
    fun addFolder(folder: SubsonicFolder): SubsonicFolder {
        if (!_subsonicFoldersMapById.contains(folder.subsonicId))
            this._subsonicFoldersMapById[folder.subsonicId] = folder
        return this._subsonicFoldersMapById[folder.subsonicId]!!
    }

    /**
     * Remove folder and its subfolder from data
     */
    @Synchronized
    fun removeFolder(folder: Folder) {
        if (this._folderSortedSet.contains(element = folder)) {
            this._folderSortedSet.remove(element = folder)
            if (folder.isSubsonic()) this._subsonicFoldersMapById.remove(key = (folder as SubsonicFolder).subsonicId)
            else this._folderMapById.remove(key = folder.id)
        }
        folder.getSubFolderSet().forEach {
            this.removeFolder(folder = it)
        }
    }

    fun getGenre(id: Long): Genre? = this._genresMapById[id]

    fun getSubsonicGenre(id: String): SubsonicGenre? = this._subsonicGenreMapById[id]

    fun getGenre(title: String): Genre? = this._genreMap[title]

    fun getGenreSet(): Set<Genre> {
        return this._genreMap.values.toSortedSet()
    }

    @Synchronized
    fun addGenre(genre: Genre): Genre {
        if (!this._genreMap.contains(key = genre.title)) {
            _genreMap[genre.title] = genre
            _genresMapById[genre.id!!] = genre
        }
        //You can have multiple same genre's name but different id, but it's the same genre.
        return _genreMap[genre.title]!!
    }

    @Synchronized
    fun addGenre(genre: SubsonicGenre): SubsonicGenre {
        if (!this._subsonicGenreMapById.contains(key = genre.subsonicId))
            this._subsonicGenreMapById[genre.subsonicId] = genre
        return this._subsonicGenreMapById[genre.subsonicId]!!
    }

    @Synchronized
    fun removeGenre(genre: Genre) {
        _genreMap.remove(genre.title)
        _genresMapById.remove(genre.id)
    }

    @Throws(LocalPlaylistNotFoundException::class)
    fun getPlaylist(id: Long): Playlist? = _playlistsMapById[id]

    fun getPlaylist(title: String): Playlist? = _playlistsMapByTitle[title]

    fun getSubsonicPlaylist(id: String): SubsonicPlaylist? = _subsonicPlaylistMapById[id]

    fun getPlaylistSet(): Set<Playlist> {
        return this._playlistsSortedMap.keys
    }

    @Synchronized
    fun addPlaylist(playlist: Playlist): Playlist {
        if (!_playlistsSortedMap.contains(playlist)) {
            _playlistsSortedMap[playlist] = playlist
            _playlistsMapById[playlist.id!!] = playlist
            _playlistsMapByTitle[playlist.title] = playlist
        }
        return this._playlistsMapById[playlist.id]!!
    }

    @Synchronized
    fun addPlaylist(playlist: SubsonicPlaylist): SubsonicPlaylist {
        if (!_subsonicPlaylistMapById.contains(key = playlist.subsonicId))
            _subsonicPlaylistMapById[playlist.subsonicId] = playlist
        return _subsonicPlaylistMapById[playlist.subsonicId]!!
    }

    @Synchronized
    fun removePlaylist(playlist: Playlist) {
        if (_playlistsSortedMap.contains(playlist)) {
            _playlistsSortedMap.remove(playlist)
            _playlistsMapById.remove(playlist.id)
            _playlistsMapByTitle.remove(playlist.title)
        }
    }

    @Synchronized
    internal fun resetAllData() {
        _musicSortedSet.clear()
        _musicsMapById.clear()
        _musicMapByAbsolutePath.clear()
        this._rootFolder = RootFolder()
        _folderMapById.clear()
        _folderSortedSet.clear()
        _artistsMapById.clear()
        _artistMap.clear()
        _albumsMapById.clear()
        _albumSortedMap.clear()
        _genresMapById.clear()
        _genreMap.clear()
        _playlistsMapById.clear()
        _playlistsMapByTitle.clear()
        _playlistsSortedMap.clear()
    }

    @Synchronized
    fun remove(music: Music) {
        this._musicsMapById.remove(music.id)
        this._musicMapByAbsolutePath.remove(music.absolutePath)
        this._musicSortedSet.remove(music)
    }
}