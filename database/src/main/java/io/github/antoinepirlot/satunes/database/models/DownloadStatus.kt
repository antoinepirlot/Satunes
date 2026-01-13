package io.github.antoinepirlot.satunes.database.models

import io.github.antoinepirlot.satunes.database.R

/**
 * @author Antoine Pirlot 23/12/2025
 */
enum class DownloadStatus(val stringId: Int) {
    NOT_DOWNLOADED(stringId = R.string.download_text),
    DOWNLOADED(stringId = R.string.downloaded_text),
    DOWNLOADING(stringId = R.string.downloading_text),
    ERROR(stringId = R.string.download_text),
}