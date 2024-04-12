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

package earth.mp3player.ui.appBars

import android.annotation.SuppressLint
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import earth.mp3player.database.models.MenuTitle
import earth.mp3player.database.services.settings.SettingsManager
import earth.mp3player.router.Destination
import earth.mp3player.ui.utils.getRightIconAndDescription

/**
 * @author Antoine Pirlot on 03/02/24
 */

@Composable
fun MP3BottomAppBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val menuTitleLists: MutableList<MenuTitle> = mutableListOf(
        MenuTitle.FOLDERS,
        MenuTitle.ARTISTS,
        MenuTitle.ALBUMS,
        MenuTitle.GENRES,
        MenuTitle.MUSICS,
        MenuTitle.PLAYLISTS
    )

    SettingsManager.menuTitleCheckedMap.forEach { (menuTitle: MenuTitle, checked: MutableState<Boolean>) ->
        if (!checked.value) {
            menuTitleLists.remove(menuTitle)
        }
    }

    val selectedMenuTitle: MutableState<MenuTitle> =
        // Update the tab by default if settings has changed
        if (SettingsManager.foldersChecked.value) {
            rememberSaveable { mutableStateOf(MenuTitle.FOLDERS) }
        } else if (SettingsManager.artistsChecked.value) {
            rememberSaveable { mutableStateOf(MenuTitle.ARTISTS) }
        } else if (SettingsManager.albumsChecked.value) {
            rememberSaveable { mutableStateOf(MenuTitle.ALBUMS) }
        } else if (SettingsManager.genresChecked.value) {
            rememberSaveable { mutableStateOf(MenuTitle.GENRES) }
        } else if (SettingsManager.playlistsChecked.value) {
            rememberSaveable { mutableStateOf(MenuTitle.PLAYLISTS) }
        } else {
            rememberSaveable { mutableStateOf(MenuTitle.MUSICS) }
        }
    val hasMaxFiveItems: Boolean = menuTitleLists.size <= 5

    NavigationBar(
        modifier = modifier
    ) {
        menuTitleLists.forEach { menuTitle: MenuTitle ->
            NavigationBarItem(
                label = {
                    if (hasMaxFiveItems) {
                        Text(text = stringResource(id = menuTitle.stringId))
                    }
                },
                selected = selectedMenuTitle.value == menuTitle,
                onClick = {
                    selectedMenuTitle.value = menuTitle
                    val rootRoute: String = when (menuTitle) {
                        MenuTitle.FOLDERS -> Destination.FOLDERS.link
                        MenuTitle.ARTISTS -> Destination.ARTISTS.link
                        MenuTitle.ALBUMS -> Destination.ALBUMS.link
                        MenuTitle.GENRES -> Destination.GENRES.link
                        MenuTitle.PLAYLISTS -> Destination.PLAYLISTS.link
                        MenuTitle.MUSICS -> Destination.MUSICS.link

                    }
                    goToRoute(
                        mediaRoute = rootRoute,
                        mediaNavController = navController,
                    )
                },
                icon = {
                    val pair = getRightIconAndDescription(menuTitle = menuTitle)

                    Icon(
                        imageVector = pair.first,
                        contentDescription = pair.second
                    )
                }
            )
        }
    }
}

/**
 * Redirect controller to the state where the user is in a bottom button's view.
 * For example, if the user click on Album button and he is in settings, then it redirects to albums.
 *
 * @param mediaRoute the media route to go
 * @param mediaNavController this nav controller is redirected to the media route
 */
private fun goToRoute(
    mediaRoute: String,
    mediaNavController: NavHostController
) {
    var currentMediaRoute: String? = mediaNavController.currentBackStackEntry?.destination?.route
    if (currentMediaRoute == mediaRoute) {
        return
    }
    currentMediaRoute = backToRoot(rootRoute = mediaRoute, navController = mediaNavController)
    if (currentMediaRoute == null || currentMediaRoute == "/") {
        //Media route could never be "/"
        mediaNavController.navigate(mediaRoute)
    }
}

/**
 * The nav controller will be roll back to the rootRoot.
 * If current route is null, then stop doing it.
 *
 * @param rootRoute the root route to go
 * @param navController the nav controller that needs to rollback
 *
 * @return the current root route of the nav controller
 */
private fun backToRoot(rootRoute: String, navController: NavHostController): String? {
    var currentRoute: String? = navController.currentBackStackEntry?.destination?.route
    while (currentRoute != null && currentRoute != rootRoute) {
        navController.popBackStack()
        currentRoute = navController.currentBackStackEntry?.destination?.route
    }
    return currentRoute
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun MainBottomAppBarPreview() {
    MP3BottomAppBar(navController = rememberNavController())
}