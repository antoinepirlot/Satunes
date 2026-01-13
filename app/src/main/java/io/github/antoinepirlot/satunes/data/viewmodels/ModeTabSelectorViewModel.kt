package io.github.antoinepirlot.satunes.data.viewmodels

import androidx.lifecycle.ViewModel
import io.github.antoinepirlot.satunes.data.states.ModeTabSelectorUiState
import io.github.antoinepirlot.satunes.models.search.ModeTabSelectorSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * @author Antoine Pirlot 09/01/2026
 */
class ModeTabSelectorViewModel: ViewModel() {
    private val _uiState: MutableStateFlow<ModeTabSelectorUiState> = MutableStateFlow(value = ModeTabSelectorUiState())

    val uiState: StateFlow<ModeTabSelectorUiState> = _uiState.asStateFlow()

    fun selectSection(section: ModeTabSelectorSection) {
        _uiState.update { currentState: ModeTabSelectorUiState ->
            currentState.copy(selectedSection = section)
        }
    }
}