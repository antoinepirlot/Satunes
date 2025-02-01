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

package io.github.antoinepirlot.satunes.ui.components.dialog.music.options

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.dialog.options.DialogOption

/**
 * @author Antoine Pirlot on 01/06/2024
 */

@Composable
internal fun NavigateToMediaMusicOption(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    satunesViewModel: SatunesViewModel = viewModel(),
    mediaImpl: MediaImpl,
) {
    val navController: NavHostController = LocalNavController.current

    DialogOption(
        modifier = modifier,
        onClick = {
            openMedia(
                playbackViewModel = playbackViewModel,
                media = mediaImpl,
                navController = navController
            )
            satunesViewModel.hideMediaOptions()
        },
        icon = when (mediaImpl) {
            is Album -> SatunesIcons.ALBUM
            is Artist -> SatunesIcons.ARTIST
            is Genre -> SatunesIcons.GENRES
            is Folder -> SatunesIcons.FOLDER
            else -> throw IllegalArgumentException("${mediaImpl.javaClass} is not allowed")
        },
        text = mediaImpl.title
    )
}

@Preview
@Composable
private fun NavigateToMediaMusicOptionPreview() {
    NavigateToMediaMusicOption(
        mediaImpl = Album(title = "Album Title", artist = Artist(title = "Artist Title")),
    )
}