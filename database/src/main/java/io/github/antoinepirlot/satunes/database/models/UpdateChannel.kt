/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  
 * If not, see <https://www.gnu.org/licenses/>.
 *  
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.database.models

import io.github.antoinepirlot.satunes.database.R

/**
 * @author Antoine Pirlot 21/04/2025
 */
enum class UpdateChannel(
    val stringId: Int,
    /**
     * Comparable order, lower number indicate greater value
     */
    val order: Int
) : Comparable<UpdateChannel> {
    ALPHA(R.string.alpha_channel_button_text, order = 0),
    BETA(R.string.beta_channel_button_text, order = 1),
    PREVIEW(R.string.preview_channel_button_text, order = 2),
    STABLE(R.string.stable_channel_button_text, order = 3);

    companion object {
        /**
         * Returns the matching UpdateChannel or throws an [IllegalArgumentException] if it was not found
         *
         * @param name [String] representing the name of [UpdateChannel]
         *
         * @return the matching [UpdateChannel]
         * @throws IllegalArgumentException if the name does not match.
         */
        fun getUpdateChannel(name: String): UpdateChannel {
            for (channel: UpdateChannel in UpdateChannel.entries)
                if (channel.name == name.uppercase()) return channel
            throw IllegalArgumentException("Wrong name: $name.")
        }
    }
}