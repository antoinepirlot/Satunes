/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.views.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Media
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.playback.services.PlaybackController
import io.github.antoinepirlot.satunes.router.utils.openCurrentMusic
import io.github.antoinepirlot.satunes.router.utils.openMedia
import io.github.antoinepirlot.satunes.ui.components.buttons.ExtraButton
import io.github.antoinepirlot.satunes.ui.components.images.AlbumArtwork
import io.github.antoinepirlot.satunes.ui.components.texts.Subtitle
import io.github.antoinepirlot.satunes.ui.components.texts.Title
import io.github.antoinepirlot.satunes.ui.views.MediaListView
import java.util.SortedMap

/**
 * @author Antoine Pirlot on 01/04/2024
 */

@Composable
fun AlbumView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    album: Album,
) {
    val playbackController: PlaybackController = PlaybackController.getInstance()

    Column(modifier = modifier.padding(top = 16.dp)) {
        AlbumArtwork(
            modifier = Modifier
                .fillMaxWidth()
                .size(250.dp),
            media = album
        )

        Title(
            bottomPadding = 0.dp,
            text = album.title
        )
        Subtitle(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    openMedia(navController = navController, media = album.artist)
                },
            text = album.artist!!.title
        )

        val musicMap: SortedMap<Music, MediaItem> = remember { album.musicMediaItemSortedMap }

        //Recompose if data changed
        var mapChanged: Boolean by remember { album.musicMediaItemSortedMapUpdate }
        if (mapChanged) {
            mapChanged = false
        }
        //

        MediaListView(
            mediaList = musicMap.keys.toList(),
            openMedia = { clickedMedia: Media ->
                playbackController.loadMusic(
                    musicMediaItemSortedMap = album.musicMediaItemSortedMap
                )
                openMedia(navController = navController, media = clickedMedia)
            },
            onFABClick = { openCurrentMusic(navController = navController) },
            extraButtons = {
                if (album.musicMediaItemSortedMap.isNotEmpty()) {
                    ExtraButton(icon = SatunesIcons.PLAY, onClick = {
                        playbackController.loadMusic(album.musicMediaItemSortedMap)
                        openMedia(navController = navController)
                    })
                    ExtraButton(icon = SatunesIcons.SHUFFLE, onClick = {
                        playbackController.loadMusic(
                            musicMediaItemSortedMap = album.musicMediaItemSortedMap,
                            shuffleMode = true
                        )
                        openMedia(navController = navController)
                    })
                }
            },
            emptyViewText = stringResource(id = R.string.no_music)
        )
    }
}

@Preview
@Composable
fun AlbumViewPreview() {
    AlbumView(
        navController = rememberNavController(),
        album = Album(
            id = 0,
            title = "Album title",
            artist = null,
            musicMediaItemSortedMap = sortedMapOf()
        )
    )
}