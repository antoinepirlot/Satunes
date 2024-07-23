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

package io.github.antoinepirlot.satunes.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.daos.LIKES_PLAYLIST_TITLE
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.Playlist
import io.github.antoinepirlot.satunes.models.SearchChips
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import java.util.SortedSet

/**
 * @author Antoine Pirlot on 21/07/2024
 */
class SearchViewModel : ViewModel() {
    private val _logger: SatunesLogger = SatunesLogger.getLogger()

    var query: String by mutableStateOf("")
        private set

    val mediaImplSet: Set<MediaImpl> = sortedSetOf()

    fun updateQuery(value: String) {
        query = value
    }

    fun search(
        dataViewModel: DataViewModel,
        selectedSearchChips: List<SearchChips>,
    ) {
        try {
            mediaImplSet as SortedSet
            mediaImplSet.clear()
            if (this.query.isBlank()) {
                // Prevent loop if string is "" or " "
                return
            }

            val query: String = this.query.lowercase()

            for (searchChip: SearchChips in selectedSearchChips) {
                dataViewModel.getMusicSet().forEach { music: Music ->
                    when (searchChip) {
                        SearchChips.MUSICS -> {
                            if (music.title.lowercase().contains(query)) {
                                if (!mediaImplSet.contains(music)) {
                                    mediaImplSet.add(element = music)
                                }
                            }
                        }

                        SearchChips.ARTISTS -> {
                            if (music.artist.title.lowercase().contains(query)) {
                                if (!mediaImplSet.contains(music.artist)) {
                                    mediaImplSet.add(element = music.artist)
                                }
                            }
                        }

                        SearchChips.ALBUMS -> {
                            if (music.album.title.lowercase().contains(query)) {
                                if (!mediaImplSet.contains(music.album)) {
                                    mediaImplSet.add(element = music.album)
                                }
                            }
                        }

                        SearchChips.GENRES -> {
                            if (music.genre.title.lowercase().contains(query)) {
                                if (!mediaImplSet.contains(music.genre)) {
                                    mediaImplSet.add(element = music.genre)
                                }
                            }
                        }

                        SearchChips.FOLDERS -> {
                            if (music.folder.title.lowercase().contains(query)) {
                                if (!mediaImplSet.contains(music.folder)) {
                                    mediaImplSet.add(element = music.folder)
                                }
                            }
                        }

                        SearchChips.PLAYLISTS -> { /* Nothing at this stage, see below */
                        }
                    }
                }
                if (searchChip == SearchChips.PLAYLISTS) {
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
        } catch (e: Throwable) {
            _logger.severe(e.message)
            throw e
        }
    }
}