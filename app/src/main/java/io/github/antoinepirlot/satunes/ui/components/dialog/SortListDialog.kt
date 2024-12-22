/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.getSortOptions
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.data.viewmodels.SortListViewModel
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.models.radio_buttons.SortOptions
import io.github.antoinepirlot.satunes.ui.components.buttons.RadioButton
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger

/**
 * @author Antoine Pirlot on 29/11/2024
 */

@Composable
internal fun SortListDialog(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    sortListViewModel: SortListViewModel = viewModel(),
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    val selectedSortOption: SortOptions = sortListViewModel.selectedSortOption
    Dialog(
        modifier = modifier,
        icon = SatunesIcons.SORT,
        title = stringResource(R.string.sort_list_title),
        onDismissRequest = {
            satunesViewModel.hideSortDialog()
            sortListViewModel.cancelSorting()
        },
        onConfirmRequest = {
            satunesViewModel.hideSortDialog()
            sortListViewModel.applySorting()
        },
        confirmText = stringResource(R.string.ok),
        dismissText = stringResource(R.string.cancel),
    ) {
        Column(modifier = Modifier.selectableGroup()) {
            val sortOptions: List<SortOptions> =
                getSortOptions(destination = satunesUiState.currentDestination)
            if (sortOptions.isEmpty()) {
                val message = "Can't sort in ${satunesUiState.currentDestination.link}"
                SatunesLogger.getLogger().severe(message)
                throw UnsupportedOperationException(message)
            }
            for (sortOption: SortOptions in sortOptions) {
                RadioButton(
                    selected = selectedSortOption == sortOption,
                    onClick = { sortListViewModel.selectSortOption(sortRadioButton = sortOption) },
                    icon = sortOption.icon,
                    text = stringResource(sortOption.stringId),
                )
            }
        }
    }
}

@Preview
@Composable
private fun SortListDialogPreview() {
    SortListDialog()
}