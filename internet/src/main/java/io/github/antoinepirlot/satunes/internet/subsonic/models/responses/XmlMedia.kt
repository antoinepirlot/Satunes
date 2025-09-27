package io.github.antoinepirlot.satunes.internet.subsonic.models.responses

import io.github.antoinepirlot.satunes.database.models.MediaImpl

/**
 * @author Antoine Pirlot 25/09/2025
 */
abstract class XmlMedia(val media: MediaImpl?): XmlObject() {
    open fun isArtist(): Boolean {
        return false
    }

    open fun isGenre(): Boolean {
        return false
    }

    open fun isSong(): Boolean {
        return false
    }

    open fun isFolder(): Boolean {
        return false
    }

    open fun isAlbum(): Boolean {
        return false
    }

    override fun isMedia(): Boolean {
        return true
    }
}