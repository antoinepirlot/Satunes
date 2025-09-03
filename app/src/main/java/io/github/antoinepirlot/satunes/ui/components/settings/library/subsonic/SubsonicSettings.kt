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

package io.github.antoinepirlot.satunes.ui.components.settings.library.subsonic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings

/**
 * @author Antoine Pirlot 03/09/2025
 */

@Composable
fun SubsonicSettings(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel()
) {
    SubSettings(
        modifier = modifier,
        title = stringResource(id = R.string.subsonic_subsonic_title),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NormalText(text = stringResource(R.string.subsonic_url_text))
        OutlinedTextField(
            value = satunesViewModel.subsonicUrl,
            onValueChange = { satunesViewModel.updateSubsonicUrl(url = it) },
            label = { NormalText(text = "URL") },
            placeholder = { NormalText(text = "https://example.org") },
            singleLine = true
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { satunesViewModel.resetSubsonicUrl() }) {
                NormalText(text = stringResource(R.string.cancel))
            }
            Button(onClick = { satunesViewModel.applySubsonicUrl() }) {
                NormalText(text = stringResource(R.string.save_button_text))
            }
        }
    }
}

@Preview
@Composable
private fun SubsonicSettingsViewPreview() {
    SubsonicSettings()
}