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

package earth.galacticmusic.ui.views.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import earth.galacticmusic.database.models.Media
import earth.galacticmusic.database.models.Music
import earth.galacticmusic.playback.services.playback.PlaybackController
import earth.galacticmusic.ui.components.cards.media.MediaCardList
import earth.galacticmusic.ui.components.music.bars.ShowCurrentMusicButton
import earth.galacticmusic.ui.components.music.buttons.ShuffleAllButton

/**
 * @author Antoine Pirlot on 01/02/24
 */

@Composable
fun <T: Comparable<T>> MediaListView(
    modifier: Modifier = Modifier,
    mediaMap: Map<T, Media>,
    openMedia: (media: Media) -> Unit,
    shuffleMusicAction: () -> Unit,
    onFABClick: () -> Unit,
    extraButtons: @Composable () -> Unit = { /*By default there's no extra buttons*/ },
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                extraButtons()
                if (PlaybackController.getInstance().musicPlaying.value != null) {
                    ShowCurrentMusicButton(onClick = onFABClick)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ShuffleAllButton(onClick = shuffleMusicAction)
            MediaCardList(mediaMap = mediaMap, openMedia = openMedia)
        }
    }
}

@Composable
@Preview
fun MediaListViewPreview() {
    val map = sortedMapOf(
        Pair<Long, Media>(
            1,
            Music(
                1,
                title = "title",
                displayName = "Musique",
                duration = 0,
                size = 0,
                relativePath = "",
                context = LocalContext.current
            )
        )
    )
    MediaListView(
        mediaMap = map,
        openMedia = {},
        shuffleMusicAction = {},
        onFABClick = {}
    )
}