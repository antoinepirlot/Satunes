package io.github.antoinepirlot.satunes.database.models.media.subsonic

import android.content.Context
import io.github.antoinepirlot.satunes.database.models.media.Playlist

/**
 * @author Antoine Pirlot 24/12/2025
 */
class SubsonicPlaylist(
    override val subsonicId: Long,
    title: String,
) : SubsonicMedia, Playlist(
    id = subsonicId,
    title = title
) {
    override fun download(context: Context) {
        TODO("Not yet implemented")
    }

    override fun removeDownload() {
        TODO("Not yet implemented")
    }
}