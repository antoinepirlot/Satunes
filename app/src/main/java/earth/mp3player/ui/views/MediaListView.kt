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
 */

package earth.mp3player.ui.views

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.models.Media
import earth.mp3player.models.Music
import earth.mp3player.services.PlaybackController
import earth.mp3player.ui.components.cards.media.MediaCardList
import earth.mp3player.ui.components.music.bars.ShowCurrentMusicButton
import earth.mp3player.ui.components.music.buttons.ShuffleAllButton
import java.util.SortedMap

@Composable
fun MediaListView(
    modifier: Modifier = Modifier,
    mediaMap: SortedMap<Long, Media>,
    openMedia: (media: Media) -> Unit,
    shuffleMusicAction: () -> Unit,
    onFABClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (PlaybackController.getInstance().musicPlaying.value != null) {
                ShowCurrentMusicButton(onClick = onFABClick)
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
    val map = sortedMapOf(Pair<Long, Media>(1, Music(1, "Musique", 0, 0, Uri.EMPTY, "", null)))
    MediaListView(
        mediaMap = map,
        openMedia = {},
        shuffleMusicAction = {},
        onFABClick = {}
    )
}