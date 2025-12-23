package io.github.antoinepirlot.satunes.ui.components.dialog.media.options

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.antoinepirlot.satunes.database.models.DownloadStatus
import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicMedia
import io.github.antoinepirlot.satunes.internet.R
import io.github.antoinepirlot.satunes.ui.components.dialog.options.DialogOption
import io.github.antoinepirlot.satunes.utils.getDownloadIcon

/**
 * @author Antoine Pirlot 23/12/2025
 */
@Composable
fun DownloadMediaOption(
    modifier: Modifier = Modifier,
    media: SubsonicMedia
) {
    val downloadStatus: DownloadStatus = media.downloadStatus

    DialogOption(
        modifier = modifier,
        enabled = downloadStatus != DownloadStatus.DOWNLOADING,
        onClick = { media.download() },
        jetpackLibsIcons = getDownloadIcon(status = downloadStatus),
        text = stringResource(id = R.string.download_text),
    )
}