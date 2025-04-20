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

package io.github.antoinepirlot.satunes.models

import io.github.antoinepirlot.satunes.R
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
import io.github.antoinepirlot.satunes.database.models.NavBarSection

/**
 *   @author Antoine Pirlot 06/03/2024
 */

// TODO rename for Switch SwitchSettings enum class
internal enum class SwitchSettings(val stringId: Int, val navBarSection: NavBarSection? = null) {
    ALBUMS_NAVBAR(stringId = albums, navBarSection = NavBarSection.ALBUMS),
    ALBUMS_FILTER(stringId = albums),
    ARTISTS_NAVBAR(stringId = artists, navBarSection = NavBarSection.ARTISTS),
    ARTISTS_FILTER(stringId = artists),
    ARTWORK_ANIMATION(stringId = R.string.artwork_animation_switch_button),
    ARTWORK_CIRCLE_SHAPE(stringId = R.string.artwork_circle_shape_switch_button),
    AUDIO_OFFLOAD(stringId = audio_offload),
    FOLDERS_NAVBAR(stringId = folders, navBarSection = NavBarSection.FOLDERS),
    FOLDERS_FILTER(stringId = folders),
    GENRES_NAVBAR(stringId = genres, navBarSection = NavBarSection.GENRES),
    GENRES_FILTER(stringId = genres),
    MUSICS_FILTER(stringId = musics),
    LOGS(stringId = R.string.logs_activation_button),
    PAUSE_IF_NOISY(stringId = pause_if_noisy),
    PLAYBACK_WHEN_CLOSED(stringId = playback_when_paused),
    PLAYLISTS_FILTER(stringId = playlists),
    PLAYLISTS_NAVBAR(stringId = playlists, navBarSection = NavBarSection.PLAYLISTS),
    PAUSE_IF_ANOTHER_PLAYBACK(stringId = pause_if_another_playback),
    COMPILATION_MUSIC(stringId = compilation_music_setting_text),
    ARTIST_REPLACEMENT(stringId = R.string.artist_replacement_setting_content),
    SHOW_FIRST_LETTER(stringId = R.string.show_first_letter_setting_content),
}