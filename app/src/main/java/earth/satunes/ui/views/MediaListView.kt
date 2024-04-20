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

package earth.satunes.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import earth.satunes.database.models.Folder
import earth.satunes.database.models.Media
import earth.satunes.database.models.Music
import earth.satunes.playback.services.PlaybackController
import earth.satunes.ui.components.cards.media.MediaCardList
import earth.satunes.ui.components.bars.ShowCurrentMusicButton
import earth.satunes.ui.components.buttons.music.ShuffleAllButton

/**
 * @author Antoine Pirlot on 01/02/24
 */

@Composable
fun MediaListView(
    modifier: Modifier = Modifier,
    mediaList: List<Media>,
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
            MediaCardList(mediaList = mediaList, openMedia = openMedia)
        }
    }
}

@Composable
@Preview
fun MediaListViewPreview() {
    val map = listOf(
        Music(
            1,
            title = "title",
            displayName = "Musique",
            duration = 0,
            size = 0,
            absolutePath = "",
            folder = Folder(title = "Folder"),
            context = LocalContext.current
        )
    )
    MediaListView(
        mediaList = map,
        openMedia = {},
        shuffleMusicAction = {},
        onFABClick = {}
    )
}