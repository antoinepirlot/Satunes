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
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.BackFolder
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.models.media.RootFolder
import io.github.antoinepirlot.satunes.database.models.media.SubsonicAlbum
import io.github.antoinepirlot.satunes.database.models.media.SubsonicArtist
import io.github.antoinepirlot.satunes.database.models.media.SubsonicFolder
import io.github.antoinepirlot.satunes.database.models.media.SubsonicGenre
import io.github.antoinepirlot.satunes.database.models.media.SubsonicMusic
import io.github.antoinepirlot.satunes.database.services.data.DataManager.subsonicMusicsMapById
import java.util.SortedMap
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 07/03/2024
 */

object DataManager {
    // All public map and sortedmap has bool state to recompose as Map are not supported for recomposition
    private var subsonicRootFolder: SubsonicFolder =
        SubsonicFolder(id = 0, title = SubsonicFolder.SUBSONIC_FOLDER_TITLE)
    private val musicSortedSet: SortedSet<Music> = sortedSetOf()
    private val musicsMapById: MutableMap<Long, Music> = mutableMapOf()
    private val musicMapByAbsolutePath: MutableMap<String, Music> = mutableMapOf()
    private val subsonicMusicsMapById: MutableMap<Long, SubsonicMusic> = mutableMapOf()
    private val subsonicMusicsSortedMap: SortedMap<SubsonicMusic, SubsonicMusic> = sortedMapOf()

    /**
     * [Collection] of [SubsonicMusic] got by the random music query
     * This [Collection] contains a maximum of 500 [SubsonicMusic] due to the API's limitation
     */
    private val subsonicRandomMusicsSortedMap: SortedMap<SubsonicMusic, SubsonicMusic> =
        sortedMapOf()

    private var _rootFolder: RootFolder = RootFolder()

    /**
     * Back folder to back in [Folder]'s tree. Always generate a new one.
     */
    private val _backFolder: BackFolder
        get() = BackFolder()
    private val folderMapById: MutableMap<Long, Folder> = mutableMapOf()
    private val folderSortedSet: SortedSet<Folder> = sortedSetOf()
    private val subsonicFoldersMapById: MutableMap<Long, SubsonicFolder> = mutableMapOf()

    private val artistsMapById: MutableMap<Long, Artist> = mutableMapOf()
    private val artistMap: SortedMap<Artist, Artist> = sortedMapOf()
    private val subsonicArtistsMapById: MutableMap<Long, SubsonicArtist> = mutableMapOf()

    private val albumsMapById: MutableMap<Long, Album> = mutableMapOf()

    // Used to know if Album is already in set. This avoid Log(N) process
    private val albumSortedMap: SortedMap<Album, Album> = sortedMapOf()
    private val subsonicAlbumsMapById: MutableMap<Long, SubsonicAlbum> = mutableMapOf()

    private val genresMapById: MutableMap<Long, Genre> = mutableMapOf()
    private val subsonicGenreMapById: MutableMap<Long, SubsonicGenre> = mutableMapOf()
    private val genreMap: SortedMap<String, Genre> = sortedMapOf()

    private val playlistsMapById: MutableMap<Long, Playlist> = mutableMapOf()
    private val playlistsMapByTitle: MutableMap<String, Playlist> = mutableMapOf()
    private val playlistsSortedMap: SortedMap<Playlist, Playlist> = sortedMapOf()
    val playlistsMapUpdated: MutableState<Boolean> = mutableStateOf(false)

    fun getMusic(id: Long): Music {
        return try {
            this.musicsMapById[id]!!
        } catch (_: NullPointerException) {
            if (this.subsonicMusicsMapById.contains(key = id))
                this.subsonicMusicsMapById[id]!!
            else
            //That means the music is not more present in the phone storage
            //Happens when the database is loaded with old information.
                throw MusicNotFoundException(id = id)
        }
    }

    fun getMusic(absolutePath: String): Music {
        return musicMapByAbsolutePath[absolutePath]!!
    }

    fun getSubsonicMusic(id: Long): SubsonicMusic? = this.subsonicMusicsMapById[id]

    fun getMusicSet(): Set<Music> {
        return this.musicSortedSet
    }

    fun getSubsonicMusicsCollection(): Collection<SubsonicMusic> {
        return this.subsonicMusicsSortedMap.keys.toSet()
    }

    fun getSubsonicRandomMusicsCollection(): Collection<SubsonicMusic> {
        return this.subsonicRandomMusicsSortedMap.keys.toSet()
    }

    fun addMusic(music: Music): Music {
        return if (music.isSubsonic()) {
            music as SubsonicMusic
            if (!this.subsonicMusicsMapById.contains(key = music.id)) {
                this.musicSortedSet.add(element = music)
                this.subsonicMusicsMapById[music.id] = music
                this.musicMapByAbsolutePath[music.absolutePath] = music
            }
            getMusic(id = music.id)
        } else {
            if (!this.musicsMapById.contains(key = music.id)) {
                this.musicSortedSet.add(element = music)
                this.musicsMapById[music.id] = music
                this.musicMapByAbsolutePath[music.absolutePath] = music
            }
            getMusic(id = music.id)
        }
    }

    /**
     * Add [musics] and add them to random music list
     *
     * @param musics the newly added [Collection] of [SubsonicMusic].
     */
    fun addRandomMusic(musics: Collection<SubsonicMusic>) {
        for (music: SubsonicMusic in musics)
            this.addRandomMusic(subsonicMusic = music)
    }

    /**
     * Add [subsonicMusic] to random music list only. Do not stores it in [subsonicMusicsMapById]
     *
     * @param subsonicMusic the newly added [SubsonicMusic]
     *
     * @return [SubsonicMusic] that has been added.
     */
    fun addRandomMusic(subsonicMusic: SubsonicMusic): SubsonicMusic {
        if (!this.subsonicRandomMusicsSortedMap.contains(key = subsonicMusic))
            this.subsonicRandomMusicsSortedMap[subsonicMusic] = subsonicMusic
        return this.subsonicRandomMusicsSortedMap[subsonicMusic]!!
    }

    /**
     * Returns the very first folder in chain.
     */
    fun getRootFolder(): RootFolder = this._rootFolder

    fun getBackFolder(): BackFolder = this._backFolder

    fun getRootSubsonicFolders(): Set<Folder> {
        return this.subsonicRootFolder.getSubFolderSet()
    }

    fun getFolderSet(): Set<Folder> {
        return this.folderSortedSet
    }

    fun getArtist(id: Long): Artist? {
        return if (this.artistsMapById.contains(key = id)) this.artistsMapById[id]!!
        else if (this.subsonicArtistsMapById.contains(key = id)) this.subsonicArtistsMapById[id]!!
        else null
    }

    fun getArtistSet(): Set<Artist> {
        return this.artistMap.keys
    }

    fun getSubsonicArtistSet(): Collection<Artist> {
        return this.subsonicArtistsMapById.values
    }

    fun getSubsonicArtist(id: Long): Artist? {
        return this.subsonicArtistsMapById[id]
    }

    fun hasSubsonicArtists(): Boolean = this.subsonicArtistsMapById.isNotEmpty()

    fun addArtist(artist: Artist): Artist {
        if (!artistMap.contains(key = artist)) {
            artistMap[artist] = artist
            if (artist.isSubsonic()) subsonicArtistsMapById[artist.id] = artist as SubsonicArtist
            else artistsMapById[artist.id] = artist
        }
        return this.artistMap[artist]!!
    }

    fun removeArtist(artist: Artist) {
        if (artistMap.contains(artist)) {
            artistMap.remove(key = artist)
            if (artist.isSubsonic()) this.subsonicArtistsMapById.remove(key = artist.id)
            else artistsMapById.remove(key = artist.id)
        }
    }

    fun getAlbum(id: Long): Album? {
        return if (this.albumsMapById.contains(key = id)) this.albumsMapById[id]!!
        else if (this.subsonicAlbumsMapById.contains(key = id)) this.subsonicAlbumsMapById[id]!!
        else null
    }

    fun getSubsonicAlbum(id: Long): Album? = this.subsonicAlbumsMapById[id]

    fun getAlbumSet(): Set<Album> {
        return this.albumSortedMap.keys
    }

    @Synchronized
    fun getSubsonicAlbumsSet(): Collection<Album> {
        return this.subsonicAlbumsMapById.values
    }

    fun hasSubsonicAlbums(): Boolean {
        return this.subsonicAlbumsMapById.isNotEmpty()
    }

    fun addAlbum(album: Album): Album {
        if (!this.albumSortedMap.contains(key = album)) {
            this.albumSortedMap[album] = album
            if (album.isSubsonic()) this.subsonicAlbumsMapById[album.id] = album as SubsonicAlbum
            else this.albumsMapById[album.id] = album
        }
        return this.albumSortedMap[album]!!
    }

    fun removeAlbum(album: Album) {
        if (this.albumSortedMap.contains(key = album)) {
            albumSortedMap.remove(key = album)
            if (album.isSubsonic()) this.subsonicAlbumsMapById.remove(key = album.id)
            else albumsMapById.remove(key = album.id)
        }
    }

    fun getFolder(id: Long): Folder? {
        return if (this.folderMapById.contains(key = id)) this.folderMapById[id]!!
        else if (this.subsonicFoldersMapById.contains(key = id)) this.subsonicFoldersMapById[id]!!
        else null
    }

    /**
     * Returns the subsonic folder if it has been created, null otherwise.
     */
    fun getSubsonicRootFolder(): Folder? {
        return this.subsonicRootFolder
    }

    fun addFolder(folder: Folder) {
        if (folder.isSubsonic()) {
            if (!folderSortedSet.contains(folder)) {
                this.folderSortedSet.add(element = folder)
                this.subsonicFoldersMapById[folder.id] = folder as SubsonicFolder
            }
        } else {
            if (!folderSortedSet.contains(folder)) {
                this.folderMapById[folder.id] = folder
                this.folderSortedSet.add(element = folder)
            }
        }

    }

    /**
     * Remove folder and its subfolder from data
     */
    fun removeFolder(folder: Folder) {
        if (this.folderSortedSet.contains(element = folder)) {
            this.folderSortedSet.remove(element = folder)
            if (folder.isSubsonic()) this.subsonicFoldersMapById.remove(key = folder.id)
            else this.folderMapById.remove(key = folder.id)
        }
        folder.getSubFolderSet().forEach {
            this.removeFolder(folder = it)
        }
    }

    fun getGenre(id: Long): Genre? {
        return if (this.genresMapById.contains(key = id)) this.genresMapById[id]!!
        else if (this.subsonicGenreMapById.contains(key = id)) this.subsonicGenreMapById[id]!!
        else null
    }

    fun getGenre(title: String): Genre? = this.genreMap[title]

    fun getGenreSet(): Set<Genre> {
        return this.genreMap.values.toSortedSet()
    }

    fun addGenre(genre: Genre): Genre {
        if (!this.genreMap.contains(key = genre.title)) {
            genreMap[genre.title] = genre
            if (genre.isSubsonic()) this.subsonicGenreMapById[genre.id] = genre as SubsonicGenre
            else genresMapById[genre.id] = genre
        }
        //You can have multiple same genre's name but different id, but it's the same genre.
        return genreMap[genre.title]!!
    }

    fun removeGenre(genre: Genre) {
        genreMap.remove(genre.title)
        genresMapById.remove(genre.id)
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
        musicsMapById.clear()
        musicMapByAbsolutePath.clear()
        this._rootFolder = RootFolder()
        folderMapById.clear()
        folderSortedSet.clear()
        artistsMapById.clear()
        artistMap.clear()
        albumsMapById.clear()
        albumSortedMap.clear()
        genresMapById.clear()
        genreMap.clear()
        playlistsMapById.clear()
        playlistsMapByTitle.clear()
        playlistsSortedMap.clear()
        playlistsMapUpdated.value = true
    }

    fun remove(music: Music) {
        this.musicsMapById.remove(music.id)
        this.musicMapByAbsolutePath.remove(music.absolutePath)
        this.musicSortedSet.remove(music)
    }
}