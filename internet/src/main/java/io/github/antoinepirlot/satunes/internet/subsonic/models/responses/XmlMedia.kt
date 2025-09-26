package io.github.antoinepirlot.satunes.internet.subsonic.models.responses

import io.github.antoinepirlot.satunes.database.models.MediaImpl

/**
 * @author Antoine Pirlot 25/09/2025
 */
abstract class XmlMedia(val media: MediaImpl): XmlObject()