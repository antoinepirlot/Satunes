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

package io.github.antoinepirlot.satunes.ui.components.settings.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.ui.components.images.ImageWithLink

/**
 * @author Antoine Pirlot on 09/09/2024
 */

@Composable
fun Fdroid(modifier: Modifier = Modifier) {
    val satunesOnFdroidUrl = "https://f-droid.org/fr/packages/io.github.antoinepirlot.satunes/"
    ImageWithLink(
        modifier = modifier,
        url = satunesOnFdroidUrl,
        shape = RectangleShape,
        painterId = R.drawable.fdroid_logo
    )
}

@Composable
private fun FdroidPreview() {
    Fdroid()
}