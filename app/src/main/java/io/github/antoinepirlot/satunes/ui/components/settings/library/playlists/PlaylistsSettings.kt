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

package io.github.antoinepirlot.satunes.ui.components.settings.library.playlists

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings
import io.github.antoinepirlot.satunes.database.R as RDb

/**
 * @author Antoine Pirlot on 27/04/2024
 */

@Composable
internal fun PlaylistsSettings(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
) {
    SubSettings(
        modifier = modifier.fillMaxWidth(),
        title = stringResource(id = RDb.string.playlists),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NormalText(
            text = stringResource(id = R.string.playlists_settings_content),
            maxLines = Int.MAX_VALUE
        )
        Spacer(modifier = Modifier.size(size = 16.dp))
        PlaylistsSettingsButtons()
    }
}

@Preview
@Composable
private fun PlaylistsSettingsViewPreview() {
    PlaylistsSettings()
}