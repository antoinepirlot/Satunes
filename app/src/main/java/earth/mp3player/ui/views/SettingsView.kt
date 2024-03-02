/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.ui.views

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.R
import earth.mp3player.models.MenuTitle
import earth.mp3player.services.SettingsManager
import kotlinx.coroutines.runBlocking

/**
 * @author Antoine Pirlot on 02-03-24
 */

@Composable
fun SettingsView(
    modifier: Modifier = Modifier
) {
    // TODO move these check to main activity and change composition of router
    val foldersChecked = rememberSaveable { SettingsManager.foldersChecked }
    val artistsChecked = rememberSaveable { SettingsManager.artistsChecked }
    val albumsChecked = rememberSaveable { SettingsManager.albumsChecked }

    val menuTitleCheckedMap: Map<MenuTitle, MutableState<Boolean>> =
        SettingsManager.menuTitleCheckedMap

    val context: Context = LocalContext.current
    LaunchedEffect(key1 = "load_settings") {
        SettingsManager.loadSettings(context = context)
    }

    Column(
        modifier = modifier
    ) {
        Box {
            Text(text = stringResource(id = R.string.bottom_bar))
            LazyColumn {
                items(
                    items = menuTitleCheckedMap.keys.toList(),
                    key = { it.stringId }
                ) { menuTitle: MenuTitle ->
                    ListItem(
                        headlineContent = {
                            Row(
                                modifier = modifier.fillMaxWidth()
                            ) {
                                Text(text = stringResource(id = menuTitle.stringId))
                                Box(
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    val checked: MutableState<Boolean> =
                                        menuTitleCheckedMap[menuTitle]!!
                                    Switch(
                                        checked = checked.value,
                                        onCheckedChange = {
                                            runBlocking {
                                                SettingsManager.switchMenuTitle(
                                                    context = context,
                                                    menuTitle = menuTitle
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun SettingsViewPreview() {
    SettingsView()
}