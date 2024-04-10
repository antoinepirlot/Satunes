/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.ui.appBars.top

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3player.router.main.MainDestination
import earth.mp3player.services.RoutersManager

/**
 * @author Antoine Pirlot on 10/04/2024
 */

@Composable
fun SecondTopAppBar(
    modifier: Modifier = Modifier,
) {
    val mainCurrentRoute: String by remember { RoutersManager.mainCurrentRoute }
    val mediaCurrentRoute: String by remember { RoutersManager.mediaCurrentRoute }
    if (canBarBeShown(mainCurrentRoute = mainCurrentRoute, mediaCurrentRoute = mediaCurrentRoute)) {
        Box(modifier = modifier.height(50.dp)) {
            Text(text = "New Second App Bar")
        }
    }

}

/**
 * Check if the bar can be shown in the actual route.
 * @return true if the bar is not in the mains routes false otherwise
 */
private fun canBarBeShown(mainCurrentRoute: String, mediaCurrentRoute: String): Boolean {
    if (mainCurrentRoute == MainDestination.SETTINGS.link) {
        return true
    }
    return mediaCurrentRoute !in RoutersManager.mediaRootRoutes
}

@Preview
@Composable
fun SecondTopAppBarPreview() {
    SecondTopAppBar()
}