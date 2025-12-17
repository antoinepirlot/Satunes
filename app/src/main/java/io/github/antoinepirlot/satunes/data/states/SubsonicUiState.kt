package io.github.antoinepirlot.satunes.data.states

import io.github.antoinepirlot.satunes.database.models.media.Media

/**
 * @author Antoine Pirlot 17/12/2025
 */
data class SubsonicUiState(
    val mediaRetrieved: Media? = null
)
