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

package io.github.antoinepirlot.satunes.data

import androidx.compose.runtime.Composable
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.database.models.custom_action.CustomActions
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.AddToPlaylistCustomAction
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.FavoriteCustomAction
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.ShareCustomAction
import io.github.antoinepirlot.satunes.ui.components.buttons.playback.custom_actions.TimerCustomAction

/**
 * @author Antoine Pirlot 05/02/2025
 */

val customActionsMap: Map<CustomActions, @Composable ((Music) -> Unit)> = mapOf(
    Pair(CustomActions.LIKE, { FavoriteCustomAction(music = it) }),
    Pair(CustomActions.ADD_TO_PLAYLIST, { AddToPlaylistCustomAction(music = it) }),
    Pair(CustomActions.SHARE, { ShareCustomAction(music = it) }),
    Pair(CustomActions.TIMER, { TimerCustomAction() })
)