package io.github.antoinepirlot.satunes.database.exceptions.media

/**
 * @author Antoine Pirlot 03/01/2026
 */
internal class CloudMusicNotFoundException(id: String) :
    MediaNotFoundException(_id = null, _cloudId = id) {
    override fun isLocal(): Boolean = false
    override fun isCloud(): Boolean = true
}