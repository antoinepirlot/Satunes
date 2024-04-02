/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.ui.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.RepeatOneOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * @author Antoine Pirlot on 02/04/2024
 */
enum class MP3PlayerIcons(val imageVector: ImageVector, val description: String) {
    ALBUM(imageVector = Icons.Rounded.Album, description = "Album Icon"),
    ARTIST(imageVector = Icons.Rounded.AccountCircle, description = "Artist Icon"),
    FOLDER(imageVector = Icons.Rounded.Folder, description = "Folder Icon"),
    GENRES(imageVector = Icons.Rounded.Category, description = "Genre Icon"),
    MUSIC(imageVector = Icons.Rounded.MusicNote, description = "Music Icon"),
    PAUSE(imageVector = Icons.Filled.PauseCircle, description = "Pause Icon"),
    PLAY(imageVector = Icons.Filled.PlayArrow, description = "Play Icon"),
    PLAYLIST(imageVector = Icons.AutoMirrored.Rounded.QueueMusic, description = "Playlist Icon"),
    PLAYLIST_ADD(
        imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
        description = "Playlist add Icon"
    ),
    PLAYLIST_REMOVE(
        imageVector = Icons.Rounded.PlaylistRemove,
        description = "Playlist Remove Icon"
    ),
    REPEAT(imageVector = Icons.Filled.Repeat, description = "Repeat Icon"),
    REPEAT_ON(imageVector = Icons.Filled.RepeatOn, description = "Repeat On"),
    REPEAT_ONE_ON(imageVector = Icons.Filled.RepeatOneOn, description = "Repeat One On Icon"),
    SETTINGS(imageVector = Icons.Filled.Settings, description = "Settings Icon"),
    SHUFFLE(imageVector = Icons.Filled.Shuffle, description = "Shuffle Icon"),
    SHUFFLE_ON(imageVector = Icons.Filled.ShuffleOn, description = "Shuffle on Icon"),
    SKIP_NEXT(imageVector = Icons.Filled.SkipNext, description = "Skip Next Icon"),
    SKIP_PREVIOUS(imageVector = Icons.Filled.SkipPrevious, description = "Skip Previous Icon"),
}