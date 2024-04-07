/*
 * This file is part of MP3 Player
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.galacticmusic.ui.views.settings

import earth.galacticmusic.R.string.closed_app_playback
import earth.galacticmusic.R.string.pause_if_noisy
import earth.galacticmusic.playback.R.string.albums
import earth.galacticmusic.playback.R.string.artists
import earth.galacticmusic.playback.R.string.folders
import earth.galacticmusic.playback.R.string.genres
import earth.galacticmusic.playback.R.string.playlists

/**
 *   @author Antoine Pirlot 06/03/2024
 */
enum class Settings(val stringId: Int) {
    CLOSED_APP_PLAYBACK(stringId = closed_app_playback),
    FOLDERS_CHECKED(stringId = folders),
    ARTISTS_CHECKED(stringId = artists),
    ALBUMS_CHECKED(stringId = albums),
    GENRES_CHECKED(stringId = genres),
    PAUSE_IF_NOISY(stringId = pause_if_noisy),
    PLAYLISTS_CHECKED(stringId = playlists),
}