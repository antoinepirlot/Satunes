package io.github.antoinepirlot.satunes.ui.components.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.antoinepirlot.jetpack_libs.components.images.Icon
import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.database.models.DownloadStatus
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia

/**
 * @author Antoine Pirlot 23/12/2025
 */
@Composable
fun MediaCloudIcon(
    modifier: Modifier = Modifier,
    subsonicMedia: SubsonicMedia
) {
    val icon: JetpackLibsIcons = when (subsonicMedia.downloadStatus) {
        DownloadStatus.NOT_DOWNLOADED -> JetpackLibsIcons.CLOUD_NOT_SAVED_ICON
        DownloadStatus.DOWNLOADING -> JetpackLibsIcons.DOWNLOADING
        DownloadStatus.DOWNLOADED -> JetpackLibsIcons.CLOUD_SAVED_ICON
        DownloadStatus.ERROR -> JetpackLibsIcons.DOWNLOAD_ERROR
    }
    Icon(modifier = modifier, jetpackLibsIcons = icon)
}