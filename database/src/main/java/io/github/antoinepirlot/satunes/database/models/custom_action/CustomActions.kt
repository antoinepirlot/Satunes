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

package io.github.antoinepirlot.satunes.database.models.custom_action

import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.satunes.database.R
import kotlinx.serialization.Serializable


/**
 * @author Antoine Pirlot 04/02/2025
 */
@Serializable
enum class CustomActions(val stringId: Int? = null, val icon: JetpackLibsIcons) {
    LIKE(icon = JetpackLibsIcons.LIKED),
    ADD_TO_PLAYLIST(
        stringId = R.string.custom_action_add_to_playlist,
        icon = JetpackLibsIcons.PLAYLIST_ADD
    ),
    SHARE(icon = JetpackLibsIcons.SHARE),
    TIMER(icon = JetpackLibsIcons.TIMER);
}