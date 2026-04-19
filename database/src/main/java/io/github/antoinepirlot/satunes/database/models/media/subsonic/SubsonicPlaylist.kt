package io.github.antoinepirlot.satunes.database.models.media.subsonic

import io.github.antoinepirlot.satunes.database.models.internet.ApiRequester
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist

/**
 * @author Antoine Pirlot 24/12/2025
 */
class SubsonicPlaylist(
    override val subsonicId: String,
    title: String,
    apiRequester: ApiRequester
) : SubsonicMedia, Playlist(
    id = null,
    title = title
) {

    private val _apiRequester: ApiRequester = apiRequester

    override var downloadedMusics: Set<SubsonicMusic> = mutableSetOf()

    override fun addMusic(music: Music) {
        if (!music.isSubsonic())
            throw IllegalArgumentException("Can't add non subsonic music into subsonic playlist")
        super.addMusic(music)
    }

    override fun getApiRequester(): ApiRequester = _apiRequester
}