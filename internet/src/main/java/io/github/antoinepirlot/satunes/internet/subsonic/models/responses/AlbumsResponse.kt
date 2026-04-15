package io.github.antoinepirlot.satunes.internet.subsonic.models.responses

import io.github.antoinepirlot.satunes.database.models.media.subsonic.SubsonicAlbum
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.media.Album
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author Antoine Pirlot on 15/04/2026
 */
@Serializable
internal class AlbumsResponse(
    @SerialName(value = "album") val albums: Collection<Album>
) {
    fun toSubsonicResponse(subsonicApiRequester: SubsonicApiRequester): Collection<SubsonicAlbum> {
        val collection: MutableCollection<SubsonicAlbum> = mutableListOf()
        this.albums.forEach { album: Album ->
            collection.add(element = album.toSubsonicMedia(subsonicApiRequester = subsonicApiRequester))
        }
        return collection
    }
}