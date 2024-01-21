package earth.mp3

import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import earth.mp3.models.Folder
import earth.mp3.models.Music
import earth.mp3.ui.theme.MP3Theme
import earth.mp3.ui.views.HomeView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        if (!isAudioDenied()) {
            setContent {
                val musicList = remember { mutableStateListOf<Music>() }
                val rootFolderList = remember { mutableStateListOf<Folder>() }

                Music.loadMusics(
                    context = LocalContext.current,
                    musicList = musicList,
                    rootFolderList = rootFolderList
                )

                MP3Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        HomeView(
                            modifier = Modifier,
                            musicList = musicList,
                            folderList = rootFolderList
                        )
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