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

package io.github.antoinepirlot.satunes.data.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.data.states.SearchUiState
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.models.StoragePlace
import io.github.antoinepirlot.satunes.models.search.SearchSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 21/07/2024
 */
class SearchViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    private val _logger: Logger? = Logger.getLogger()
    private val _filtersList: MutableMap<SearchChips, Boolean> = mutableMapOf(
        Pair(SearchChips.MUSICS, SettingsManager.musicsFilter),
        Pair(SearchChips.ALBUMS, SettingsManager.albumsFilter),
        Pair(SearchChips.ARTISTS, SettingsManager.artistsFilter),
        Pair(SearchChips.GENRES, SettingsManager.genresFilter),
        Pair(SearchChips.FOLDERS, SettingsManager.foldersFilter),
        Pair(SearchChips.PLAYLISTS, SettingsManager.playlistsFilter),
    )

    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val selectedSearchChips: MutableList<SearchChips> = mutableStateListOf()

    var query: String by mutableStateOf("")
        private set

    init {
        selectedSearchChips.addAll(_filtersList.filter { it.value }.keys)
    }

    fun updateQuery(value: String) {
        query = value
    }

        try {
            runBlocking {
                selectedSearchChips.clear()
                SettingsManager.loadFilters(context = MainActivity.instance.applicationContext)
                _filtersList[SearchChips.MUSICS] = SettingsManager.musicsFilter
                _filtersList[SearchChips.ALBUMS] = SettingsManager.albumsFilter
                _filtersList[SearchChips.ARTISTS] = SettingsManager.artistsFilter
                _filtersList[SearchChips.GENRES] = SettingsManager.genresFilter
                _filtersList[SearchChips.FOLDERS] = SettingsManager.foldersFilter
                _filtersList[SearchChips.PLAYLISTS] = SettingsManager.playlistsFilter
                _filtersList.forEach { (searchChip: SearchChips, checked: Boolean) ->
                    if (checked) {
                        selectedSearchChips.add(searchChip)
                    }
                }
            }
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }

    fun select(searchChip: SearchChips) {
        _filtersList[searchChip] = true
        if (!selectedSearchChips.contains(element = searchChip)) {
            selectedSearchChips.add(searchChip)
        }
    }

    fun unselect(searchChip: SearchChips) {
        _filtersList[searchChip] = false
        selectedSearchChips.remove(searchChip)
    }

    fun switchFilter(filterSetting: NavBarSection) {
        try {
            runBlocking {
                SettingsManager.switchFilter(
                    context = MainActivity.instance.applicationContext,
                    filterSetting = filterSetting
                )
                _uiState.update { currentState: SearchUiState ->
                    currentState.copy(
                        musicsFilter = SettingsManager.musicsFilter,
                        foldersFilter = SettingsManager.foldersFilter,
                        artistsFilter = SettingsManager.artistsFilter,
                        albumsFilter = SettingsManager.albumsFilter,
                        genresFilter = SettingsManager.genresFilter,
                        playlistsFilter = SettingsManager.playlistsFilter,
                    )
                }
            }
        } catch (e: Throwable) {
            _logger?.severe(e.message)
            throw e
        }
    }

    /**
     * Search matching [MediaImpl] with the [query].
     *
     * These media are sorted.
     *
     * If the query is blank, no results.
     *
     * @param storagePlace where the search algorithm must search.
     * @param dataViewModel the [DataViewModel] where to get the local list of all [MediaImpl] stored locally.
     * @param selectedSearchChips the [Collection] of [SearchChips] to know which kind of [MediaImpl] to include in search.
     */
    fun search(
        storagePlace: StoragePlace,
        dataViewModel: DataViewModel,
        selectedSearchChips: Collection<SearchChips>,
    ) {
        if (this.query.isNotBlank())
            when (storagePlace) {
                StoragePlace.LOCAL -> localSearch(
                    dataViewModel = dataViewModel,
                    selectedSearchChips = selectedSearchChips
                )

                StoragePlace.SUBSONIC -> subsonicSearch()
            }
        else
            dataViewModel.loadMediaImplList(list = sortedSetOf())
    }

    /**
     * Search matching [MediaImpl] with the [query].
     * It searches on media stored on the user's devices and not elsewhere.
     *
     * These media are sorted.
     *
     * @param dataViewModel the [DataViewModel] where to get the local list of all [MediaImpl] stored locally
     * @param selectedSearchChips the [Collection] of [SearchChips] to know which kind of [MediaImpl] to include in search.
     */
    private fun localSearch(
        dataViewModel: DataViewModel,
        selectedSearchChips: Collection<SearchChips>,
    ) {
        val mediaImplSet: SortedSet<MediaImpl> = sortedSetOf()
        val query: String = this.query.trim().lowercase()
        val musicSet: Set<Music> = dataViewModel.getMusicSet()
        val artistSet: Set<Artist> = dataViewModel.getArtistSet()
        val albumSet: Set<Album> = dataViewModel.getAlbumSet()
        val genreSet: Set<Genre> = dataViewModel.getGenreSet()
        val folderSet: Set<Folder> = dataViewModel.getFolderSet()
        val playlistSet: Set<Playlist> = dataViewModel.getPlaylistSet()

        for (searchChip: SearchChips in selectedSearchChips) {
            when (searchChip) {
                SearchChips.MUSICS -> {
                    musicSet.forEach { music: Music ->
                        if (music.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = music)
                        }
                    }
                }

                SearchChips.ARTISTS -> {
                    artistSet.forEach { artist: Artist ->
                        if (artist.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = artist)
                        }
                    }
                }

                SearchChips.ALBUMS -> {
                    albumSet.forEach { album: Album ->
                        if (album.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = album)
                        }
                    }
                }

                SearchChips.GENRES -> {
                    genreSet.forEach { genre: Genre ->
                        if (genre.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = genre)
                        }
                    }
                }

                SearchChips.FOLDERS -> {
                    folderSet.forEach { folder: Folder ->
                        if (folder.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = folder)
                        }
                    }
                }

                SearchChips.PLAYLISTS -> {
                    playlistSet.forEach { playlist: Playlist ->
                        val context = MainActivity.instance.applicationContext
                        if (playlist.title == LIKES_PLAYLIST_TITLE) {
                            playlist.title = context.getString(R.string.likes_playlist_title)
                        }
                        if (playlist.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = playlist)
                        }
                    }
                }
            }
        }
        dataViewModel.loadMediaImplList(list = mediaImplSet)
    }

    /**
     * Search matching [MediaImpl] with the [query].
     * It request the subsonic api to search according to the query.
     *
     * These media are sorted.
     */
    private fun subsonicSearch() {

    }

    fun selectSection(selectedSection: SearchSection) {
        _uiState.update { currentState: SearchUiState ->
            currentState.copy(selectedSection = selectedSection)
        }
    }
}