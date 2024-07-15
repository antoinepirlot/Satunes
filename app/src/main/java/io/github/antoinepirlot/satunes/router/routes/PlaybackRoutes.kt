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

package io.github.antoinepirlot.satunes.router.routes

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.services.DataLoader
import io.github.antoinepirlot.satunes.router.Destination
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.views.LoadingView
import io.github.antoinepirlot.satunes.ui.views.playback.PlaybackView
import io.github.antoinepirlot.satunes.ui.views.playback.common.PlaybackQueueView

/**
 * @author Antoine Pirlot on 15/07/2024
 */

fun NavGraphBuilder.playbackRoutes(
    navController: NavHostController,
    onStart: AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(Destination.PLAYBACK.link) {
        onStart(it)
        val isLoading: Boolean by rememberSaveable { DataLoader.isLoading }
        val isLoaded: Boolean by rememberSaveable { DataLoader.isLoaded }
        if (isLoading || !isLoaded) {
            LoadingView()
        } else {
            PlaybackView(
                navController = navController,
                onAlbumClick = { album: Album? ->
                    if (album != null) {
                        openMedia(media = album, navController = navController)
                    }
                },
                onArtistClick = { artist: Artist ->
                    openMedia(media = artist, navController = navController)
                }
            )
        }
    }

    composable(Destination.PLAYBACK_QUEUE.link) {
        onStart(it)
        PlaybackQueueView(navController = navController)
    }
}