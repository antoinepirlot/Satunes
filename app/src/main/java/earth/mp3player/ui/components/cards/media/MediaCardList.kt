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

 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.ui.components.cards.media

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.R
import earth.mp3player.database.models.Folder
import earth.mp3player.database.models.Media
import earth.mp3player.database.models.relations.PlaylistWithMusics
import earth.mp3player.database.models.tables.MusicDB
import earth.mp3player.ui.utils.getRightIconAndDescription

/**
 * @author Antoine Pirlot on 16/01/24
 */

@Composable
fun <T : Comparable<T>> MediaCardList(
    modifier: Modifier = Modifier,
    mediaMap: Map<T, Media>,
    openMedia: (media: Media) -> Unit
) {
    val lazyState = rememberLazyListState()

    if (mediaMap.isEmpty()) {
        // It fixes issue while accessing last folder in chain
        return
    }

    Column {
        LazyColumn(
            modifier = modifier,
            state = lazyState
        ) {
            items(
                items = mediaMap.values.toList(),
                key = { it.id }
            ) { media: Media ->
                // First pair is image vector and second one is content description (String)
                val pair = getRightIconAndDescription(media)
                val mediaName: String =
                    if (media is Folder && media.parentFolder == null) {
                        when (media.title) {
                            "0" -> stringResource(id = R.string.this_device)

                            else -> "${stringResource(id = R.string.external_storage)}: ${media.title}"
                        }
                    } else if (media is PlaylistWithMusics) {
                        media.playlist.title
                    } else if (media is MusicDB) {
                        media.music.title
                    } else {
                        media.title
                    }

                MediaCard(
                    modifier = modifier,
                    text = mediaName,
                    imageVector = pair.first,
                    contentDescription = pair.second,
                    onClick = { openMedia(media) }
                )
            }
        }
    }
}

@Composable
@Preview
fun <T : Comparable<T>> CardListPreview() {
    MediaCardList(
        mediaMap = sortedMapOf<T, Media>(),
        openMedia = {}
    )
}