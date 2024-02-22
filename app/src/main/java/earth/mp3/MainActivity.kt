package earth.mp3

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Music
import earth.mp3.router.Destination
import earth.mp3.router.Router
import earth.mp3.services.DataLoader
import earth.mp3.services.PlaybackController
import earth.mp3.ui.appBars.MP3BottomAppBar
import earth.mp3.ui.appBars.MP3TopAppBar
import earth.mp3.ui.theme.MP3Theme

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
                val folderListToShow = remember { mutableStateListOf<Folder>() }
                val musicMapToShow = remember { mutableStateMapOf<Long, Music>() }
                val artistListToShow = remember { mutableStateListOf<Artist>() }

                DataLoader.loadAllData(
                    context = LocalContext.current,
                    musicMediaItemSortedMap = musicMediaItemSortedMap,
                    rootFolderMap = rootFolderList,
                    folderMap = folderMap,
                    artistMap = artistMap,
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