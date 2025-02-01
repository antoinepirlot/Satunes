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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.data.local

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 25/07/2024
 */

val LocalSnackBarHostState: ProvidableCompositionLocal<SnackbarHostState> =
    compositionLocalOf { error("No SnackbarStateHost provided.") }
val LocalMainScope: ProvidableCompositionLocal<CoroutineScope> =
    compositionLocalOf { error("No CoroutineScope provided.") }
val LocalNavController: ProvidableCompositionLocal<NavHostController> =
    compositionLocalOf { error("No NavHostController provided.") }