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

package io.github.antoinepirlot.satunes.ui.views.playback

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.jetpack_libs.components.models.ScreenSizes
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.router.utils.getNavBarSectionDestination
import io.github.antoinepirlot.satunes.ui.components.bars.backToRoot
import io.github.antoinepirlot.satunes.ui.views.playback.mobile.PlaybackMobileView
import io.github.antoinepirlot.satunes.ui.views.playback.tablet.PlaybackTabletView

/**
 * @author Antoine Pirlot on 27/06/2024
 */

@Composable
internal fun PlaybackView(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    onAlbumClick: (album: Album?) -> Unit,
    onArtistClick: (artist: Artist) -> Unit,
) {
    val navController: NavHostController = LocalNavController.current

    if (playbackViewModel.musicPlaying == null) {
        backToRoot(
            rootRoute = getNavBarSectionDestination(navBarSection = satunesViewModel.defaultNavBarSection).link,
            navController = navController
        )
        return
    }
    /**
     * If tablet -> playbackTabletView
     * else -> playbackMobileView
     */
    val screenWidthDp: Int = LocalConfiguration.current.screenWidthDp
    if (screenWidthDp < ScreenSizes.LARGE) {
        // Mobile
        PlaybackMobileView(
            modifier = modifier,
            onAlbumClick = onAlbumClick,
            onArtistClick = onArtistClick
        )
    } else {
        // Tablet
        PlaybackTabletView(
            modifier = modifier,
            onAlbumClick = onAlbumClick,
            onArtistClick = onArtistClick
        )
    }
}

@Preview
@Composable
private fun PlaybackViewPreview() {
    PlaybackView(onAlbumClick = {}, onArtistClick = {})
}