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

package io.github.antoinepirlot.satunes.playback.models

import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import io.github.antoinepirlot.satunes.icons.R

/**
 * @author Antoine Pirlot on 08/09/2024
 */
@UnstableApi
enum class CustomCommands(val commandButton: CommandButton) {
    SHUFFLE_ON(
        commandButton = CommandButton.Builder()
            .setDisplayName("Shuffle on")
            .setIconResId(R.drawable.shuffle_on)
            .setSessionCommand(PlaybackSessionCallback.SHUFFLE_COMMAND)
            .build()
    ),
    SHUFFLE_OFF(
        commandButton = CommandButton.Builder()
            .setDisplayName("Shuffle off")
            .setIconResId(R.drawable.shuffle_off)
            .setSessionCommand(PlaybackSessionCallback.SHUFFLE_COMMAND)
            .build()
    ),
    REPEAT_OFF(
        commandButton = CommandButton.Builder()
            .setDisplayName("Repeat off")
            .setIconResId(R.drawable.repeat_off)
            .setSessionCommand(PlaybackSessionCallback.REPEAT_COMMAND)
            .build(),
    ),
    REPEAT_ON(
        commandButton = CommandButton.Builder()
            .setDisplayName("Repeat on")
            .setIconResId(R.drawable.repeat_on)
            .setSessionCommand(PlaybackSessionCallback.REPEAT_COMMAND)
            .build(),
    ),
    REPEAT_ONE_ON(
        commandButton = CommandButton.Builder()
            .setDisplayName("Repeat one on")
            .setIconResId(R.drawable.repeat_off)
            .setSessionCommand(PlaybackSessionCallback.REPEAT_COMMAND)
            .build(),
    ),
}