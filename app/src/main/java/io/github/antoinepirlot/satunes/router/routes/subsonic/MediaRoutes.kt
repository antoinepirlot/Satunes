package io.github.antoinepirlot.satunes.router.routes.subsonic

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.NavigationViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.media.Media

/**
 * @author Antoine Pirlot 17/12/2025
 */
internal fun NavGraphBuilder.mediaRoutes(
    satunesViewModel: SatunesViewModel,
    dataViewModel: DataViewModel,
    subsonicViewModel: SubsonicViewModel,
    navigationViewModel: NavigationViewModel,
    onStart: (NavBackStackEntry) -> Unit,
    onMediaOpen: (media: Media) -> Unit
) {
    localMediaRoutes(
        satunesViewModel = satunesViewModel,
        dataViewModel = dataViewModel,
        onStart = onStart,
        onMediaOpen = onMediaOpen,
    )

    subsonicMediaRoutes(
        subsonicViewModel = subsonicViewModel,
        navigationViewModel = navigationViewModel,
        dataViewModel = dataViewModel,
        onStart = onStart,
        onMediaOpen = onMediaOpen,
    )
}