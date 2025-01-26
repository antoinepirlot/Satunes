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

package io.github.antoinepirlot.satunes.ui.components.bars.bottom

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton

/**
 * Extra Button list to show on scaffold.
 *
 * On click, it will load the playback with the set.
 * The [musicSet] or the [mediaImplSet] must not be null. Both can't be null and both can't be define, only one is null!
 *
 * @param modifier the [Modifier].
 * @param playbackViewModel the [PlaybackViewModel] initialized by default.
 * @param mediaImplSet the [Set] of [MediaImpl] to load. ([Music] [Set] must be null if it is not null).
 * @param musicSet the [Set] of [Music] to load. ([MediaImpl] [Set] must be null if it is not null).
 */
@Composable
internal fun ExtraButtonList(
    modifier: Modifier = Modifier,
    playbackViewModel: PlaybackViewModel = viewModel(),
    mediaImplSet: Set<MediaImpl>?,
    musicSet: Set<Music>?,
) {
    if (musicSet == null && mediaImplSet == null) throw IllegalArgumentException("Music set and MediaImplSet can't be both null.")
    if (musicSet != null && mediaImplSet != null) throw IllegalArgumentException("Music set and MediaImplSet can't be both not null.")

    val navController: NavHostController = LocalNavController.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExtraButton(
            icon = SatunesIcons.PLAY,
            onClick = {
                if (mediaImplSet == null)
                    playbackViewModel.loadMusic(musicSet = musicSet!!)
                else
                    playbackViewModel.loadMusicFromMedias(mediaImplSet)
                openMedia(
                    playbackViewModel = playbackViewModel,
                    navController = navController
                )
            }
        )
        ExtraButton(
            icon = SatunesIcons.SHUFFLE,
            onClick = {
                if (mediaImplSet == null)
                    playbackViewModel.loadMusic(
                        musicSet = musicSet!!,
                        shuffleMode = true
                    )
                else
                    playbackViewModel.loadMusicFromMedias(
                        medias = mediaImplSet,
                        shuffleMode = true
                    )
                openMedia(
                    playbackViewModel = playbackViewModel,
                    navController = navController
                )
            }
        )
    }
}