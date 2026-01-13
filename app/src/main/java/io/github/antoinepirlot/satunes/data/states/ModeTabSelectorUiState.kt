package io.github.antoinepirlot.satunes.data.states

import io.github.antoinepirlot.satunes.models.search.ModeTabSelectorSection

/**
 * @author Antoine Pirlot 09/01/2026
 */
data class ModeTabSelectorUiState(
    val selectedSection: ModeTabSelectorSection = ModeTabSelectorSection.DEFAULT
)