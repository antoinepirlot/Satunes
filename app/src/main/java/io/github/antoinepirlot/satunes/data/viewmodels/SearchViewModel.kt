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

package io.github.antoinepirlot.satunes.data.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.data.states.SearchUiState
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
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
    private val _logger: SatunesLogger = SatunesLogger.getLogger()
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

    val mediaImplSet: SortedSet<MediaImpl> = sortedSetOf()

    //Used to recompose
    var mediaImplSetHasChanged: Boolean by mutableStateOf(false)
        private set

    init {
        selectedSearchChips.addAll(_filtersList.filter { it.value }.keys)
    }

    fun updateQuery(value: String) {
        query = value
    }

    fun resetSelectedChips() {
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
            _logger.severe(e.message)
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
            _logger.severe(e.message)
            throw e
        }
    }

    fun search(
        dataViewModel: DataViewModel,
        selectedSearchChips: List<SearchChips>,
    ) {
        mediaImplSet.clear()
        if (this.query.isBlank()) {
            // Prevent loop if string is "" or " "
            mediaImplSetHasChanged = true
            return
        }

        val query: String = this.query.trim().lowercase()

        for (searchChip: SearchChips in selectedSearchChips) {
            when (searchChip) {
                SearchChips.MUSICS -> {
                    dataViewModel.getMusicSet().forEach { music: Music ->
                        if (music.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = music)
                        }
                    }
                }

                SearchChips.ARTISTS -> {
                    dataViewModel.getArtistSet().forEach { artist: Artist ->
                        if (artist.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = artist)
                        }
                    }
                }

                SearchChips.ALBUMS -> {
                    dataViewModel.getAlbumSet().forEach { album: Album ->
                        if (album.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = album)
                        }
                    }
                }

                SearchChips.GENRES -> {
                    dataViewModel.getGenreSet().forEach { genre: Genre ->
                        if (genre.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = genre)
                        }
                    }
                }

                SearchChips.FOLDERS -> {
                    dataViewModel.getFolderSet().forEach { folder: Folder ->
                        if (folder.title.lowercase().contains(query)) {
                            mediaImplSet.add(element = folder)
                        }
                    }
                }

                SearchChips.PLAYLISTS -> {
                    dataViewModel.getPlaylistSet().forEach { playlist: Playlist ->
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
        mediaImplSetHasChanged = true
    }

    //Used to recompose
    fun mediaImplChangeUpdated() {
        mediaImplSetHasChanged = false
    }
}