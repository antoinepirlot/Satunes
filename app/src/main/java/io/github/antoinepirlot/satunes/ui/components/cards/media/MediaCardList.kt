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
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.cards.media

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.database.models.media.BackFolder
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions
import io.github.antoinepirlot.satunes.ui.components.dialog.media.MediaOptionsDialog

/**
 * @author Antoine Pirlot on 16/01/2024
 */

@Composable
internal fun MediaCardList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel(),
    mediaImplList: List<MediaImpl>,
    header: @Composable (() -> Unit)? = null,
    scrollToMusicPlaying: Boolean = false,
    showGroupIndication: Boolean = true,
    onMediaClick: ((MediaImpl) -> Unit)? = null,
) {
    val navigationUiState: NavigationUiState by navigationViewModel.uiState.collectAsState()
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    val showFirstLetter: Boolean = dataUiState.showFirstLetter
    val navController: NavHostController = LocalNavController.current
    val haptics: HapticFeedback = LocalHapticFeedback.current
    val isInPlaybackView: Boolean = navigationViewModel.isInPlaybackView()

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        items(
            items = mediaImplList,
            key = { it.javaClass.name + '-' + it.id }
        ) { mediaImpl: MediaImpl ->
            val isFirst: Boolean = mediaImpl == mediaImplList.first()
            if (isFirst) header?.invoke()

            if (showFirstLetter && showGroupIndication) {
                if (isFirst)
                    Spacer(Modifier.size(size = 16.dp))
                Indicator(mediaImpl = mediaImpl, mediaImplList = mediaImplList)
            }

            var showMediaOptions: Boolean by rememberSaveable { mutableStateOf(false) }
            MediaCard(
                modifier = modifier,
                mediaImpl = mediaImpl,
                onClick = {
                    if (onMediaClick != null) {
                        onMediaClick.invoke(mediaImpl)
                    } else {
                        if (mediaImpl.isBackFolder()) {
                            mediaImpl as BackFolder
                            if (mediaImpl.hasBeenClicked()) return@MediaCard
                            val currentMediaImpl: Folder =
                                navigationUiState.currentMediaImpl as Folder
                            navigationViewModel.navigate(
                                navController = navController,
                                mediaImpl = currentMediaImpl.parentFolder
                            )
                            mediaImpl.clicked()
                            return@MediaCard
                        }
                        if (mediaImpl.isMusic() && !isInPlaybackView)
                            playbackViewModel.loadMusicFromMedias(
                                medias = mediaImplList,
                                currentDestination = navigationUiState.currentDestination,
                                musicToPlay = mediaImpl as Music?
                            )
                        navigationViewModel.openMedia(
                            playbackViewModel = playbackViewModel,
                            media = mediaImpl,
                            navController = if (isInPlaybackView) null else navController
                        )
                    }
                },
                onLongClick = {
                    if (mediaImpl.isBackFolder()) return@MediaCard
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    showMediaOptions = true
                }
            )

            // Media option dialog
            if (showMediaOptions) {
                MediaOptionsDialog(
                    mediaImpl = mediaImpl,
                    onDismissRequest = {
                        showMediaOptions = false
                    }
                )
            }
        }
    }
    if (scrollToMusicPlaying) {
        LaunchedEffect(key1 = Unit) {
            lazyListState.scrollToItem(
                playbackViewModel.getMusicPlayingIndexPosition()
            )
        }
    }
}

/**
 * Show the indicator for the filtered list.
 *
 * For example, if sorting by letters, it will show each first different letter on screen.
 *
 * (Sorry, I don't know what to call it... that's the hardest part of a developer's job: naming things).
 */
@Composable
private fun Indicator(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    mediaImpl: MediaImpl,
    mediaImplList: List<MediaImpl>
) {
    val sortOption: SortOptions = dataViewModel.sortOption

    FirstElementCard {
        when (sortOption) {
            SortOptions.ARTIST, SortOptions.ALBUM, SortOptions.GENRE -> {
                FirstMedia(
                    modifier = modifier,
                    mediaImpl = mediaImpl,
                    mediaImplList = mediaImplList,
                    sortOptions = sortOption
                )
            }

            SortOptions.YEAR -> {
                FirstYear(
                    modifier = modifier,
                    mediaImpl = mediaImpl,
                    mediaImplList = mediaImplList
                )
            }

            else -> {
                FirstLetter(
                    modifier = modifier,
                    mediaImpl = mediaImpl,
                    mediaImplList = mediaImplList,
                    sortOption = sortOption
                )
            }
        }
    }
}

@Composable
@Preview
private fun CardListPreview() {
    MediaCardList(mediaImplList = listOf(), header = {}, scrollToMusicPlaying = false)
}