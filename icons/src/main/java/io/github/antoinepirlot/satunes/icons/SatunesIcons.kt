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

package io.github.antoinepirlot.satunes.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddToQueue
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.BatterySaver
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOff
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LogoDev
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material.icons.rounded.QueuePlayNext
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Update
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * @author Antoine Pirlot on 02/04/2024
 */
enum class SatunesIcons(val imageVector: ImageVector, val description: String) {
    ADD(imageVector = Icons.Rounded.Add, description = "Add Icon"),
    ADD_TO_PLAYBACK_QUEUE(
        imageVector = Icons.Rounded.AddToQueue,
        description = "Add to queue Icon"
    ),
    ALBUM(imageVector = Icons.Rounded.Album, description = "Album Icon"),
    ANDROID_AUTO(
        imageVector = Icons.Rounded.DirectionsCar,
        description = "Android Auto Setting Icon"
    ),
    ARTIST(imageVector = Icons.Rounded.AccountCircle, description = "Artist Icon"),
    BATTERY(imageVector = Icons.Rounded.BatterySaver, description = "Battery Icon"),
    BOTTOM_BAR(imageVector = Icons.Rounded.Navigation, description = "Bottom Bar Setting Icon"),
    CHIP_SELECTED(imageVector = Icons.Rounded.Done, description = "Chip Selected icon"),
    INFO(imageVector = Icons.Rounded.Info, description = "Info Icon"),
    LIKED(imageVector = Icons.Rounded.Favorite, description = "Liked Icon"),
    FOLDER(imageVector = Icons.Rounded.Folder, description = "Folder Icon"),
    GENRES(imageVector = Icons.Rounded.Category, description = "Genre Icon"),
    MUSIC(imageVector = Icons.Rounded.MusicNote, description = "Music Icon"),
    MUSIC_PLAYING(imageVector = Icons.Rounded.GraphicEq, description = "Music Playing Icon"),
    PAUSE(imageVector = Icons.Rounded.PauseCircle, description = "Pause Icon"),
    PERMISSION_GRANTED(imageVector = Icons.Rounded.Done, description = "Permission Granted Icon"),
    PERMISSION_NOT_GRANTED(
        imageVector = Icons.Rounded.Close,
        description = "Permission Not Granted Icon"
    ),
    PLAY(imageVector = Icons.Rounded.PlayCircle, description = "Play Icon"),
    PLAY_NEXT(imageVector = Icons.Rounded.QueuePlayNext, description = "Play Next Icon"),
    PLAYBACK(imageVector = Icons.AutoMirrored.Rounded.PlaylistPlay, description = "Playback Icon"),
    PLAYLIST(imageVector = Icons.AutoMirrored.Rounded.QueueMusic, description = "Playlist Icon"),
    PLAYLIST_ADD(
        imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
        description = "Playlist add Icon"
    ),
    PLAYLIST_REMOVE(
        imageVector = Icons.Rounded.PlaylistRemove,
        description = "Playlist Remove Icon"
    ),
    REPEAT(imageVector = Icons.Rounded.Repeat, description = "Repeat Icon"),
    REPEAT_ONE(imageVector = Icons.Rounded.RepeatOne, description = "Repeat One On Icon"),
    SEARCH(imageVector = Icons.Rounded.Search, description = "Search Icon"),
    SETTINGS(imageVector = Icons.Rounded.Settings, description = "Settings Icon"),
    SETTING_FOLDERS(
        imageVector = Icons.Rounded.FolderOff,
        description = "Folders Setting Icon"
    ),
    SETTING_LOGS(imageVector = Icons.Rounded.LogoDev, description = "Log Setting Icon"),
    SETTING_PERMISSIONS(
        imageVector = Icons.Rounded.Security,
        description = "Permission Setting Icon"
    ),
    SETTING_UPDATE(imageVector = Icons.Rounded.Update, description = "Update Setting Icon"),
    SHUFFLE(imageVector = Icons.Rounded.Shuffle, description = "Shuffle Icon"),
    SKIP_NEXT(imageVector = Icons.Rounded.SkipNext, description = "Skip Next Icon"),
    SKIP_PREVIOUS(imageVector = Icons.Rounded.SkipPrevious, description = "Skip Previous Icon"),
    UNLIKED(imageVector = Icons.Rounded.FavoriteBorder, description = "Unliked Icon"),
    REMOVE_ICON(imageVector = Icons.Rounded.Delete, description = "Remove Icon"),
}