package io.github.antoinepirlot.satunes.internet.subsonic.models.responses

import io.github.antoinepirlot.satunes.database.models.Music

/**
 * @author Antoine Pirlot 25/09/2025
 */
class XmlSong(val music: Music): XmlMedia(media = music)