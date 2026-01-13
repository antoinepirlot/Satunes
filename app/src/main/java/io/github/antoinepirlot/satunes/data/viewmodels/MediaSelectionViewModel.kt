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

import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.data.states.MediaSelectionUiState
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * @author Antoine Pirlot on 30/03/2024
 */
class MediaSelectionViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<MediaSelectionUiState> = MutableStateFlow(
        MediaSelectionUiState()
    )

    /**
     * List of checked playlists to know where to add music from form
     */
    private var _checkedPlaylistWithMusics: MutableList<Playlist> = mutableListOf()
    private var _checkedMusics: MutableList<Music> = mutableListOf()

    /**
     * Use to know from where the selection is running.
     * If the user add multiple [Music] to a single [Playlist], then it must be a [Playlist]
     * If the user add a single [Music] to multiple [Playlist] then it must be a [Music]
     */
    private var _currentMediaImpl: MediaImpl? = null

    val uiState: StateFlow<MediaSelectionUiState> = _uiState.asStateFlow()

    fun clearAll() {
        this.clearCheckedMusics()
        this.clearCheckedPlaylistWithMusics()
        this._currentMediaImpl = null
    }

    fun getCheckedPlaylistWithMusics(): List<Playlist> {
        val list: List<Playlist> = _checkedPlaylistWithMusics
        clearCheckedPlaylistWithMusics()
        return list
    }

    fun addPlaylist(playlist: Playlist) {
        _checkedPlaylistWithMusics.add(playlist)
    }

    fun removePlaylist(playlist: Playlist) {
        _checkedPlaylistWithMusics.remove(playlist)
    }

    private fun clearCheckedPlaylistWithMusics() {
        _checkedPlaylistWithMusics = mutableListOf()
    }

    fun getCheckedMusics(): List<Music> {
        val list: List<Music> = _checkedMusics
        clearCheckedMusics()
        return list
    }

    fun addMusic(music: Music) {
        _checkedMusics.add(music)
    }

    fun removeMusic(music: Music) {
        _checkedMusics.remove(music)
    }

    private fun clearCheckedMusics() {
        _checkedMusics = mutableListOf()
    }

    fun setShowPlaylistCreation(value: Boolean) {
        _uiState.update { currentState: MediaSelectionUiState ->
            currentState.copy(showPlaylistCreation = value)
        }
    }

    /**
     * Checks if the mediaImpl is selected. If it was already present in the [_currentMediaImpl]
     * then it add it to [_checkedMusics] or [_checkedPlaylistWithMusics].
     */
    fun isChecked(mediaImpl: MediaImpl): Boolean {
        return if (mediaImpl.isMusic()) {
            mediaImpl as Music
            //Check in playlist
            if ((_currentMediaImpl as Playlist).contains(media = mediaImpl)) {
                this.addMusic(music = mediaImpl)
                true
            } else this._checkedMusics.contains(element = mediaImpl)
        } else if (mediaImpl.isPlaylist()) {
            mediaImpl as Playlist
            //Check for selected media
            if (mediaImpl.contains(media = _currentMediaImpl!!)) {
                this.addPlaylist(playlist = mediaImpl)
                true
            } else this._checkedPlaylistWithMusics.contains(element = mediaImpl)
        } else throw IllegalArgumentException("The media is not a music or playlist")
    }

    fun setCurrentMediaImpl(mediaImpl: MediaImpl) {
        this._currentMediaImpl = mediaImpl
    }
}