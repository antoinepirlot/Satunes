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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.states.DataUiState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.PlaybackViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.MediaImpl
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.dialog.media.MediaOptionsDialog

/**
 * @author Antoine Pirlot on 16/01/2024
 */

@Composable
internal fun MediaCardList(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    lazyListState: LazyListState = rememberLazyListState(),
    dataViewModel: DataViewModel = viewModel(),
    playbackViewModel: PlaybackViewModel = viewModel(),
    mediaImplList: List<MediaImpl>,
    header: @Composable (() -> Unit)? = null,
    scrollToMusicPlaying: Boolean = false,
    showGroupIndication: Boolean = true,
    onMediaClick: ((MediaImpl) -> Unit)? = null
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val dataUiState: DataUiState by dataViewModel.uiState.collectAsState()
    val showFirstLetter: Boolean = dataUiState.showFirstLetter
    val sortOption: SortOptions = dataViewModel.sortOption
    val navController: NavHostController = LocalNavController.current
    val haptics: HapticFeedback = LocalHapticFeedback.current
    val isInPlaybackView: Boolean = satunesViewModel.isInPlaybackView()

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        //Used to store dynamically the first media impl linked to the first occurrence of a letter or media impl.
        val groupMap: MutableMap<Any?, MediaImpl> = mutableMapOf()

        items(
            items = mediaImplList,
            key = { it.javaClass.name + '-' + it.id }
        ) { mediaImpl: MediaImpl ->
            if (mediaImpl == mediaImplList.first()) header?.invoke()

            if (showFirstLetter && showGroupIndication) {
                when (sortOption) {
                    SortOptions.ARTIST, SortOptions.ALBUM, SortOptions.GENRE -> {
                        FirstElementCard {
                            FirstMedia(
                                map = groupMap,
                                mediaImpl = mediaImpl,
                                mediaImplList = mediaImplList,
                                sortOptions = sortOption
                            )
                        }
                    }

                    SortOptions.YEAR -> FirstElementCard {
                        FirstYear(
                            map = groupMap,
                            mediaImpl = mediaImpl,
                            mediaImplList = mediaImplList
                        )
                    }

                    else -> {
                        FirstElementCard {
                            FirstLetter(
                                map = groupMap,
                                mediaImpl = mediaImpl,
                                mediaImplList = mediaImplList,
                                sortOption = sortOption
                            )
                        }
                    }
                }
            }

            var showMediaOptions: Boolean by rememberSaveable { mutableStateOf(false) }
            MediaCard(
                modifier = modifier,
                mediaImpl = mediaImpl,
                onClick = {
                    if (onMediaClick != null) {
                        onMediaClick.invoke(mediaImpl)
                    } else {
                        if (mediaImpl is Folder && mediaImpl.isBackFolder()) {
                            navController.popBackStack()
                            return@MediaCard
                        }
                        if (mediaImpl is Music && !isInPlaybackView)
                            playbackViewModel.loadMusicFromMedias(
                                medias = mediaImplList,
                                currentDestination = satunesUiState.currentDestination,
                                musicToPlay = mediaImpl
                            )
                        openMedia(
                            playbackViewModel = playbackViewModel,
                            media = mediaImpl,
                            navController = if (isInPlaybackView) null else navController
                        )
                    }
                },
                onLongClick = {
                    if (mediaImpl is Folder && mediaImpl.isBackFolder()) return@MediaCard
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

@Composable
@Preview
private fun CardListPreview() {
    MediaCardList(mediaImplList = listOf(), header = {}, scrollToMusicPlaying = false)
}