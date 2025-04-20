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

package io.github.antoinepirlot.satunes.car.playback

import io.github.antoinepirlot.satunes.car.pages.ScreenPages
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger

/**
 * @author Antoine Pirlot on 17/03/2024
 */
internal object RouteManager {
    private const val DEFAULT_IS_SHUFFLE_BUTTON_SELECTED: Boolean = false
    private val _logger: SatunesLogger? = SatunesLogger.getLogger()
    private var _selectedTab: ScreenPages? = null
    private var _selectedMediaImpl: MediaImpl? = null
    private var _isShuffleButtonSelected: Boolean = DEFAULT_IS_SHUFFLE_BUTTON_SELECTED

    fun isSelected(): Boolean {
        return this._selectedTab == this._selectedMediaImpl // That means both are null
    }

    fun reset() {
        this._selectedMediaImpl = null
        this._selectedTab = null
        this._isShuffleButtonSelected = DEFAULT_IS_SHUFFLE_BUTTON_SELECTED
    }

    fun selectMedia(route: String) {
        try {
            val mediaId: Long = route.toLong()
            //This is a media other than music
            _selectedMediaImpl = getSelectedMediaImpl(mediaId = mediaId)
        } catch (e: NumberFormatException) {
            //This is a tab
            _selectedTab = getTab(route = route)
        }
    }

    fun getSelectedMediaImpl(): MediaImpl? = this._selectedMediaImpl

    fun getSelectedTab(): ScreenPages = this._selectedTab!!

    fun isShuffleButtonSelected(): Boolean = this._isShuffleButtonSelected

    fun setShuffleButtonSelected(selected: Boolean) {
        this._isShuffleButtonSelected = selected
    }

    private fun getTab(route: String): ScreenPages {
        return when (route) {
            ScreenPages.ALL_FOLDERS.id -> ScreenPages.ALL_FOLDERS
            ScreenPages.ALL_ALBUMS.id -> ScreenPages.ALL_ALBUMS
            ScreenPages.ALL_ARTISTS.id -> ScreenPages.ALL_ARTISTS
            ScreenPages.ALL_GENRES.id -> ScreenPages.ALL_GENRES
            ScreenPages.ALL_PLAYLISTS.id -> ScreenPages.ALL_PLAYLISTS
            ScreenPages.ALL_MUSICS.id -> ScreenPages.ALL_MUSICS
            else -> {
                throw IllegalArgumentException("It's not a tab.")
            }
        }
    }

    private fun getSelectedMediaImpl(mediaId: Long): MediaImpl? {
        try {
            return when (_selectedTab) {
                ScreenPages.ALL_FOLDERS -> DataManager.getFolder(id = mediaId)
                ScreenPages.ALL_ALBUMS -> DataManager.getAlbum(id = mediaId)
                ScreenPages.ALL_ARTISTS -> DataManager.getArtist(id = mediaId)
                ScreenPages.ALL_GENRES -> DataManager.getGenre(id = mediaId)
                ScreenPages.ALL_PLAYLISTS -> DataManager.getPlaylist(id = mediaId)
                ScreenPages.ALL_MUSICS -> DataManager.getMusic(id = mediaId)
                else -> {
                    throw IllegalStateException("The selected tab is not a media tab")
                }
            }
        } catch (e: Throwable) {
            _logger?.severe(e.message ?: "An error occurred while getting selected media impl.")
            throw e
        }
    }
}