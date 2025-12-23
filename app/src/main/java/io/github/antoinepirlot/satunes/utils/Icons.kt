package io.github.antoinepirlot.satunes.utils

import io.github.antoinepirlot.jetpack_libs.models.JetpackLibsIcons
import io.github.antoinepirlot.satunes.database.models.DownloadStatus

/**
 * @author Antoine Pirlot 23/12/2025
 */

fun getDownloadIcon(status: DownloadStatus): JetpackLibsIcons {
    return when (status) {
        DownloadStatus.NOT_DOWNLOADED -> JetpackLibsIcons.DOWNLOAD
        DownloadStatus.DOWNLOADING -> JetpackLibsIcons.DOWNLOADING
        DownloadStatus.DOWNLOADED -> JetpackLibsIcons.DOWNLOADED
        DownloadStatus.ERROR -> JetpackLibsIcons.DOWNLOAD_ERROR
    }
}