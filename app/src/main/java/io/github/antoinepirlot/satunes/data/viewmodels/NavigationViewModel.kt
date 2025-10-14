/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import io.github.antoinepirlot.satunes.data.states.NavigationUiState
import io.github.antoinepirlot.satunes.database.models.media.Album
import io.github.antoinepirlot.satunes.database.models.media.Artist
import io.github.antoinepirlot.satunes.database.models.media.Folder
import io.github.antoinepirlot.satunes.database.models.media.Genre
import io.github.antoinepirlot.satunes.database.models.media.MediaImpl
import io.github.antoinepirlot.satunes.database.models.media.Music
import io.github.antoinepirlot.satunes.database.models.media.Playlist
import io.github.antoinepirlot.satunes.database.services.data.DataManager
import io.github.antoinepirlot.satunes.database.services.settings.SettingsManager
import io.github.antoinepirlot.satunes.models.Destination
import io.github.antoinepirlot.satunes.models.DestinationCategory
import io.github.antoinepirlot.satunes.models.listeners.OnDestinationChangedListener
import io.github.antoinepirlot.satunes.router.utils.getNavBarSectionDestination
import io.github.antoinepirlot.satunes.ui.utils.startMusic
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.ArrayDeque
import java.util.Deque

/**
 * @author Antoine Pirlot 13/10/2025
 */
class NavigationViewModel : ViewModel() {
    companion object {
        private val _uiState: MutableStateFlow<NavigationUiState> =
            MutableStateFlow(NavigationUiState())

        private var _initialised: Boolean = false
        private val routesStack: Deque<Pair<Destination, MediaImpl?>> =
            ArrayDeque() //TODO change structure as it add at first place

        private fun push(destination: Destination, mediaImpl: MediaImpl?) {
            routesStack.push(Pair(first = destination, second = mediaImpl))
            updateUiState()
            OnDestinationChangedListener.incrementDepth()
        }

        private fun pop(): Pair<Destination, MediaImpl?>? {
            if (this.routesStack.isEmpty()) return null //To avoid crashing app if leaving with back gesture
            OnDestinationChangedListener.decrementDepth()
            val popped: Pair<Destination, MediaImpl?> = routesStack.pop()
            updateUiState()
            return popped
        }

        private fun updateUiState() {
            _uiState.update { currentState: NavigationUiState ->
                currentState.copy(
                    currentMediaImpl = getCurrentMediaImpl(),
                    currentDestination = getCurrentDestination()
                )
            }
        }

        private fun contains(destination: Destination, mediaImpl: MediaImpl?): Boolean {
            for (pair: Pair<Destination, MediaImpl?> in this.routesStack)
                if (pair.first == destination && pair.second == mediaImpl)
                    return true
            return false
        }

        private fun getCurrentDestination(): Destination {
            return this.routesStack.peekFirst()?.first
                ?: getNavBarSectionDestination(navBarSection = SettingsManager.defaultNavBarSection.value)
        }

        private fun getCurrentMediaImpl(): MediaImpl? {
            return this.routesStack.peekFirst()?.second
        }
    }

    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    fun stackSize(): Int = routesStack.size

    fun init(defaultDestination: Destination) {
        if (_initialised)
            throw IllegalStateException("Can't initialise the NavigationViewModel twice")
        if (defaultDestination == Destination.FOLDERS)
            push(destination = defaultDestination, mediaImpl = DataManager.getRootFolder())
        else
            push(destination = defaultDestination, mediaImpl = null)
        _initialised = true
    }

    fun navigate(
        navController: NavController,
        destination: Destination
    ) {
        push(destination = destination, mediaImpl = null)
        navController.navigate(route = destination.link)
    }

    fun navigate(
        navController: NavController,
        mediaImpl: MediaImpl?
    ) {
        val destination: Destination = this.getDestinationOf(mediaImpl = mediaImpl)
        push(destination = destination, mediaImpl = mediaImpl)
        val route: String =
            if (mediaImpl == null || destination == Destination.PLAYBACK) destination.link
            else this.getRoute(destination = destination, mediaImpl = mediaImpl)
        navController.navigate(route = route)
    }

    fun popBackStack(navController: NavController): Pair<Destination, MediaImpl?>? {
        navController.popBackStack()
        return pop()
    }

    fun contains(destination: Destination, mediaImpl: MediaImpl?): Boolean {
        return Companion.contains(destination = destination, mediaImpl = mediaImpl)
    }

    /**
     * Redirect controller to the state where the user is in a bottom button's view.
     * For example, if the user click on Album button and he is in settings, then it redirects to albums.
     *
     * @param rootRoute the root route to go
     */
    fun backToRoot(
        rootRoute: Destination,
        navController: NavController
    ) {
        var currentRoute: String? = navController.currentBackStackEntry?.destination?.route
        if (currentRoute == rootRoute.link) return
        while (currentRoute != null) {
            this.popBackStack(navController = navController)
            currentRoute = navController.currentBackStackEntry?.destination?.route
        }
        this.navigate(navController = navController, destination = rootRoute)
    }

    /**
     * Return the destination of mediaImpl (folder, artists or music).
     *
     * @param mediaImpl the mediaImpl to get the destination
     *
     * @return the destination matching [MediaImpl]
     */
    private fun getDestinationOf(mediaImpl: MediaImpl?): Destination {
        return when (mediaImpl) {
            is Folder -> Destination.FOLDERS
            is Artist -> Destination.ARTISTS
            is Album -> Destination.ALBUMS
            is Genre -> Destination.GENRES
            is Playlist -> Destination.PLAYLISTS
            else -> Destination.PLAYBACK
        }
    }

    private fun getRoute(destination: Destination, mediaImpl: MediaImpl): String {
        return "${destination.link}/${mediaImpl.id}"
    }

    /**
     * Open the mediaImpl, when it is:
     *      Music: navigate to the media's destination and start music with exoplayer
     *
     *      Folder: navigate to the media's destination
     *
     *      Artist: navigate to the media's destination
     *
     * @param media the mediaImpl to open
     */
    fun openMedia(
        playbackViewModel: PlaybackViewModel,
        media: MediaImpl? = null,
        navController: NavController?,
        reset: Boolean = false
    ) {
        if (media == null || media is Music)
            startMusic(playbackViewModel = playbackViewModel, mediaToPlay = media, reset = reset)

        if (navController != null)
            this.navigate(navController = navController, mediaImpl = media)
    }


    /**
     * Open the current playing music
     *
     * @throws IllegalStateException if there's no music playing
     */
    fun openCurrentMusic(playbackViewModel: PlaybackViewModel, navController: NavController) {
        val musicPlaying: Music? = playbackViewModel.musicPlaying
        if (musicPlaying == null) {
            val message = "No music is currently playing, this button can be accessible"
            SatunesLogger.getLogger()?.severe(message)
            throw IllegalStateException(message)
        }
        this.navigate(navController = navController, mediaImpl = musicPlaying)
    }

    fun isInPlaybackView(): Boolean {
        return getCurrentDestination().category == DestinationCategory.PLAYBACK
    }
}