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

package earth.satunes.ui.views.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import earth.satunes.R
import earth.satunes.router.Destination
import earth.satunes.ui.components.buttons.ClickableListItem
import earth.satunes.ui.components.texts.Title

/**
 * @author Antoine Pirlot on 02/03/24
 */

@Composable
fun SettingsView(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier) {
        Title(text = stringResource(id = R.string.settings))
        HorizontalDivider()
        Column(modifier = Modifier.verticalScroll(state = scrollState)) {
            ClickableListItem(text = stringResource(id = R.string.bottom_bar), onClick = {
                navController.navigate(Destination.BOTTOM_BAR_SETTING.link)
            })
            HorizontalDivider()
            ClickableListItem(text = stringResource(id = R.string.playback_settings), onClick = {
                navController.navigate(Destination.PLAYBACK_SETTINGS.link)
            })
            HorizontalDivider()
            ClickableListItem(text = stringResource(id = R.string.version), onClick = {
               navController.navigate(Destination.UPDATES.link)
            })
            HorizontalDivider()
            AboutView()
        }
    }
}

@Composable
@Preview
fun SettingsViewPreview() {
    SettingsView(navController = rememberNavController())
}