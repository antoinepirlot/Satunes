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

package io.github.antoinepirlot.satunes.ui.components.cards.media

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music

/**
 * Show the first [Genre]'s title of the [MediaImpl] if it is the first occurrence in the list.
 *
 * @param map the map containing the [Genre] as key and the [MediaImpl] as the value.
 * @param mediaImpl the [MediaImpl] used to be checked
 * @param mediaImplList the [List] of [MediaImpl] where to check the first occurrence
 * @author Antoine Pirlot 27/01/2025
 */
@Composable
fun FirstGenre(
    map: MutableMap<Any?, MediaImpl>,
    mediaImpl: Music,
    mediaImplList: Collection<MediaImpl>
) {
    val mediaImplToCompare: Genre = mediaImpl.genre
    if (!map.containsKey(mediaImplToCompare))
        map[mediaImplToCompare] = mediaImplList.first { (it as Music).genre == mediaImplToCompare }
    if (mediaImpl == map[mediaImplToCompare]) {
        Title(
            modifier = Modifier.padding(vertical = 15.dp),
            bottomPadding = 0.dp,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            text = mediaImplToCompare.title
        )
    }
}