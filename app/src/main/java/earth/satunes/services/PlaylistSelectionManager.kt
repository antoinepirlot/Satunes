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

package earth.satunes.services

import earth.satunes.database.models.relations.PlaylistWithMusics

/**
 * @author Antoine Pirlot on 30/03/2024
 */
internal object PlaylistSelectionManager {
    /**
     * Mutable list of checked playlists' ids to know where to add music from form
     */
    val checkedPlaylistWithMusics: MutableList<PlaylistWithMusics> = mutableListOf()

    /**
     * Contains the playlist the user is viewing or null if the user is elsewhere
     */
    var openedPlaylist: PlaylistWithMusics? = null
}