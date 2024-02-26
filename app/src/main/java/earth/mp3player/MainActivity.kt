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

package earth.mp3player

import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import earth.mp3player.models.Album
import earth.mp3player.models.Artist
import earth.mp3player.models.Folder
import earth.mp3player.models.Music
import earth.mp3player.router.Destination
import earth.mp3player.router.Router
import earth.mp3player.services.DataLoader
import earth.mp3player.services.PlaybackController
import earth.mp3player.ui.appBars.MP3BottomAppBar
import earth.mp3player.ui.appBars.MP3TopAppBar
import earth.mp3player.ui.theme.MP3Theme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        if (!isAudioDenied()) {
            setContent {
                val musicMediaItemSortedMap = remember { sortedMapOf<Music, MediaItem>() }
                val rootFolderList = remember { sortedMapOf<Long, Folder>() }
                val folderMap = remember { sortedMapOf<Long, Folder>() }
                val artistMap = remember { sortedMapOf<String, Artist>() }
                val albumMap = remember { sortedMapOf<Long, Album>() }

                DataLoader.loadAllData(
                    context = LocalContext.current,
                    musicMediaItemSortedMap = musicMediaItemSortedMap,
                    rootFolderMap = rootFolderList,
                    folderMap = folderMap,
                    artistMap = artistMap,
                    albumMap = albumMap
                )

                PlaybackController.initInstance(
                    context = LocalContext.current,
                    musicMediaItemSortedMap = musicMediaItemSortedMap
                )

                MP3Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val scrollBehavior =
                            TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                        val startDestination =
                            rememberSaveable { mutableStateOf(Destination.FOLDERS.link) }

                        Scaffold(
                            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                            topBar = { MP3TopAppBar(scrollBehavior = scrollBehavior) },
                            bottomBar = { MP3BottomAppBar(startDestination = startDestination) }
                        ) { innerPadding ->
                            Column(
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                Router(
                                    startDestination = startDestination.value,
                                    rootFolderMap = rootFolderList,
                                    folderMap = folderMap,
                                    allArtistSortedMap = artistMap,
                                    allMusicMediaItemsMap = musicMediaItemSortedMap,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissionLauncher(): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        when {
            isAudioDenied() -> {
                requestPermissionLauncher().launch(READ_MEDIA_AUDIO)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this, READ_MEDIA_AUDIO) -> {
                // Additional rationale should be displayed
            }

            else -> {
                // Permission has not been asked yet
                requestPermissionLauncher().launch(READ_MEDIA_AUDIO)
            }
        }
    }

    private fun isAudioDenied(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_DENIED
    }
}