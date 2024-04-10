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
import earth.mp3player.playback.models.MenuTitle
import earth.mp3player.playback.services.settings.SettingsManager
import earth.mp3player.router.main.MainDestination
import earth.mp3player.router.media.MediaDestination
import earth.mp3player.ui.utils.getRightIconAndDescription

/**
 * @author Antoine Pirlot on 03/02/24
 */

@Composable
fun MP3BottomAppBar(
    modifier: Modifier = Modifier,
    mediaNavController: NavHostController,
    mainNavController: NavHostController,
) {
    if (mediaNavController == mainNavController) {
        throw IllegalArgumentException("Media and Main nav controller can't be the same")
    }
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
                    val currentMainRoute: String =
                        mainNavController.currentBackStackEntry!!.destination.route!!
                    if (currentMainRoute == MainDestination.SETTINGS.link) {
                        mainNavController.popBackStack()
                    }
                    when (menuTitle) {
                        MenuTitle.FOLDERS -> {
                            val rootRoute: String = MediaDestination.FOLDERS.link
                            backToRoot(rootRoute = rootRoute, navController = mediaNavController)
                        }

                        MenuTitle.ARTISTS -> {
                            val rootRoute: String = MediaDestination.ARTISTS.link
                            backToRoot(rootRoute = rootRoute, navController = mediaNavController)
                        }

                        MenuTitle.ALBUMS -> {
                            val rootRoute: String = MediaDestination.ALBUMS.link
                            backToRoot(rootRoute = rootRoute, navController = mediaNavController)
                        }

                        MenuTitle.GENRES -> {
                            val rootRoute: String = MediaDestination.GENRES.link
                            backToRoot(rootRoute = rootRoute, navController = mediaNavController)
                        }

                        MenuTitle.PLAYLISTS -> {
                            val rootRoute: String = MediaDestination.PLAYLISTS.link
                            backToRoot(rootRoute = rootRoute, navController = mediaNavController)
                        }

                        MenuTitle.MUSICS -> {
                            val rootRoute: String = MediaDestination.MUSICS.link
                            backToRoot(rootRoute = rootRoute, navController = mediaNavController)
                        }

                    }
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

private fun backToRoot(rootRoute: String, navController: NavHostController) {
    var currentRoute: String? = navController.currentBackStackEntry!!.destination.route!!
    while (currentRoute != null && currentRoute != MainDestination.ROOT.link && currentRoute != rootRoute) {
        navController.popBackStack()
        currentRoute = try {
            navController.currentBackStackEntry!!.destination.route!!
        } catch (_: NullPointerException) {
            null
        }
    }
    if (currentRoute == null || (currentRoute == MainDestination.ROOT.link && rootRoute != MainDestination.ROOT.link)) {
        navController.navigate(rootRoute)
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun MainBottomAppBarPreview() {
    MP3BottomAppBar(
        mainNavController = rememberNavController(),
        mediaNavController = rememberNavController(),
    )
}