package io.github.antoinepirlot.satunes.data.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * @author Antoine Pirlot 03/01/2026
 */
class PlaylistCreationFormViewModel : ViewModel() {
    var title: String by mutableStateOf(value = "")
        private set
    var isStoringOnCloud: Boolean by mutableStateOf(value = false)
        private set

    fun updateTitle(value: String) {
        title = value
    }

    fun switchIsStoringOnCloud() {
        this.isStoringOnCloud = !this.isStoringOnCloud
    }
}