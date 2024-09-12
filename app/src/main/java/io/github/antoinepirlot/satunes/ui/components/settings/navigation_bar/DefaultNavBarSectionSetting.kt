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

package io.github.antoinepirlot.satunes.ui.components.settings.navigation_bar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.allNavBarSections
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.database.models.NavBarSection
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.ui.components.images.Icon

/**
 * @author Antoine Pirlot on 29/08/2024
 */

@Composable
internal fun DefaultNavBarSectionSetting(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()

    var expanded: Boolean by remember { mutableStateOf(false) }
    val selectedSection: NavBarSection = satunesUiState.defaultNavBarSection

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        NormalText(text = stringResource(id = R.string.default_section_setting_content))
        Box {
            TextButton(onClick = { expanded = true }) {
                Row {
                    Icon(
                        icon = if (expanded) SatunesIcons.CLOSE_DROPDOWN_MENU
                        else SatunesIcons.OPEN_DROPDOWN_MENU
                    )
                    NormalText(text = stringResource(id = selectedSection.stringId))
                }
            }
            Menu(expanded = expanded, onDismissRequest = { expanded = false })
        }
    }
}

@Composable
private fun Menu(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
    expanded: Boolean,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        for (navBarSection: NavBarSection in allNavBarSections) {
            if (navBarSection.isEnabled) {
                DropdownMenuItem(
                    text = { NormalText(text = stringResource(id = navBarSection.stringId)) },
                    onClick = {
                        satunesViewModel.selectDefaultNavBarSection(navBarSection = navBarSection)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun DefaultNavBarSectionSettingPreview() {
    DefaultNavBarSectionSetting()
}