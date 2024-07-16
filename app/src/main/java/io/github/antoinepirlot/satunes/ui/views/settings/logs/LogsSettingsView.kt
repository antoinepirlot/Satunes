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

package io.github.antoinepirlot.satunes.ui.views.settings.logs

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.components.texts.Title

/**
 * @author Antoine Pirlot on 15/07/2024
 */

@Composable
internal fun LogsSettingsView(
    modifier: Modifier = Modifier,
) {
    val scrollState: ScrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(state = scrollState),
    ) {
        Title(text = stringResource(id = R.string.logs_settings))

        NormalText(
            text = stringResource(id = R.string.logs_settings_content),
            maxLines = Int.MAX_VALUE
        )

        Button(onClick = { /*TODO*/ }) {
            NormalText(text = stringResource(id = R.string.export))
        }
    }
}

@Preview
@Composable
private fun LogsSettingsViewPreview() {
    LogsSettingsView()
}