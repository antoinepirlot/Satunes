package io.github.antoinepirlot.satunes.internet.subsonic.models.responses

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicPlaylist
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Playlist
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot 09/01/2026
 */
@Serializable
internal data class PlaylistsResponse(
    @SerialName(value = "playlist") val playlists: Collection<Playlist>
) {
    fun toSubsonicResponse(subsonicApiRequester: SubsonicApiRequester): Collection<SubsonicPlaylist> {
        val collection: MutableCollection<SubsonicPlaylist> = mutableListOf()
        this.playlists.forEach { playlist: Playlist ->
            collection.add(element = playlist.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester))
        }
        return collection
    }
}