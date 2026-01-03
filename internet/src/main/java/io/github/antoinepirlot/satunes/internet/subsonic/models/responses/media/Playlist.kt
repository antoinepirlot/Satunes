package io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.utils.getOrCreateSubsonicPlaylist
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 26/12/2025
 */
@Serializable
internal data class Playlist(
    @SerialName(value = "id") val id: String,
    @SerialName(value = "name") val title: String,
    @SerialName(value = "owner") val owner: String,
    @SerialName(value = "public") val public: Boolean,
    @SerialName(value = "songCount") val songCount: Long,
    @SerialName(value = "duration") val duration: Long,
    @SerialName(value = "created") val creationDate: String,
    @SerialName(value = "entry") val entries: Collection<Song>
) : SubsonicData {
    override fun toSubsonicMedia(subsonicApiRequester: SubsonicApiRequester): SubsonicPlaylist {
        val playlist: SubsonicPlaylist =
            getOrCreateSubsonicPlaylist(id = this.id, title = this.title)
        //TODO entries
        return playlist
    }
}