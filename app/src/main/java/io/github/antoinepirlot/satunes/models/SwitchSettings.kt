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

package io.github.antoinepirlot.satunes.models

import io.github.antoinepirlot.satunes.R.string.audio_offload
import io.github.antoinepirlot.satunes.R.string.compilation_music_setting_text
import io.github.antoinepirlot.satunes.R.string.pause_if_another_playback
import io.github.antoinepirlot.satunes.R.string.pause_if_noisy
import io.github.antoinepirlot.satunes.R.string.playback_when_paused
import io.github.antoinepirlot.satunes.database.R.string.albums
import io.github.antoinepirlot.satunes.database.R.string.artists
import io.github.antoinepirlot.satunes.database.R.string.folders
import io.github.antoinepirlot.satunes.database.R.string.genres
import io.github.antoinepirlot.satunes.database.R.string.musics
import io.github.antoinepirlot.satunes.database.R.string.playlists

/**
 *   @author Antoine Pirlot 06/03/2024
 */

// TODO rename for Switch SwitchSettings enum class
internal enum class SwitchSettings(val stringId: Int) {
    ALBUMS_CHECKED(stringId = albums),
    ALBUMS_FILTER(stringId = albums),
    ARTISTS_CHECKED(stringId = artists),
    ARTISTS_FILTER(stringId = artists),
    AUDIO_OFFLOAD(stringId = audio_offload),
    PLAYLISTS_FILTER(stringId = playlists),
    FOLDERS_CHECKED(stringId = folders),
    FOLDERS_FILTER(stringId = folders),
    GENRES_CHECKED(stringId = genres),
    GENRES_FILTER(stringId = genres),
    MUSICS_FILTER(stringId = musics),
    PAUSE_IF_NOISY(stringId = pause_if_noisy),
    PLAYBACK_WHEN_CLOSED(stringId = playback_when_paused),
    PLAYLISTS_CHECKED(stringId = playlists),
    PAUSE_IF_ANOTHER_PLAYBACK(stringId = pause_if_another_playback),
    COMPILATION_MUSIC(stringId = compilation_music_setting_text),
}