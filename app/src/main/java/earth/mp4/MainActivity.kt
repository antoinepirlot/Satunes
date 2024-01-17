package earth.mp4

import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import earth.mp4.ui.home.HomeView
import earth.mp4.ui.theme.MP3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        setContent {
            MP3Theme {
                HomeView(modifier = Modifier.fillMaxSize())
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
            ContextCompat.checkSelfPermission(
                this,
                READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_DENIED -> {
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
}