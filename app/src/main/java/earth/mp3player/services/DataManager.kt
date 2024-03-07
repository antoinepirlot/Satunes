/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.services

import androidx.media3.common.MediaItem
import earth.mp3player.models.Album
import earth.mp3player.models.Artist
import earth.mp3player.models.Folder
import earth.mp3player.models.Genre
import earth.mp3player.models.Music
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 07/03/2024
 */

object DataManager {
    val musicMediaItemSortedMap: SortedMap<Music, MediaItem> = sortedMapOf()
    val rootFolderMap: SortedMap<Long, Folder> = sortedMapOf()
    val folderMap: SortedMap<Long, Folder> = sortedMapOf()
    val artistMap: SortedMap<String, Artist> = sortedMapOf()
    val albumMap: SortedMap<String, Album> = sortedMapOf()
    val genreMap: SortedMap<String, Genre> = sortedMapOf()
}