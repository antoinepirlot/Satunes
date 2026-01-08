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
    @SerialName(value = "owner") val owner: String? = null,
    @SerialName(value = "public") val public: Boolean? = null,
    @SerialName(value = "songCount") val songCount: Long? = null,
    @SerialName(value = "duration") val duration: Long? = null,
    @SerialName(value = "created") val creationDate: String? = null,
    @SerialName(value = "entry") val entries: Collection<Song> = listOf(),
    @SerialName(value = "coverArt") val artwork: String? = null,
    @SerialName(value = "changed") val lastChanged: String? = null,
) : SubsonicData {
    override fun toSubsonicMedia(subsonicApiRequester: SubsonicApiRequester): SubsonicPlaylist {
        val playlist: SubsonicPlaylist =
            getOrCreateSubsonicPlaylist(id = this.id, title = this.title)
        //TODO entries
        return playlist
    }
}