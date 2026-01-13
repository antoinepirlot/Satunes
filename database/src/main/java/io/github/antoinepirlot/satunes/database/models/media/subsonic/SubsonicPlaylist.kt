package io.github.antoinepirlot.satunes.database.models.media.subsonic

import android.content.Context
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist

/**
 * @author Antoine Pirlot 24/12/2025
 */
class SubsonicPlaylist(
    override val subsonicId: String,
    id: Long?,
    title: String,
) : SubsonicMedia, Playlist(
    id = id,
    title = title
) {

    override fun addMusic(music: Music) {
        if (!music.isSubsonic())
            throw IllegalArgumentException("Can't add non subsonic music into subsonic playlist")
        super.addMusic(music)
    }

    override fun download(context: Context) {
        TODO("Not yet implemented")
    }

    override fun removeDownload() {
        TODO("Not yet implemented")
    }
}