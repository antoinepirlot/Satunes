/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 */

package io.github.antoinepirlot.satunes.ui.components.buttons.settings.reset

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.ui.components.buttons.ButtonWithIcon

/**
 * @author Antoine Pirlot on 22/11/2024
 */

@Composable
internal fun ResetButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    onClick: () -> Unit,
) {
    var jetpackLibsIcons: JetpackLibsIcons by rememberSaveable { mutableStateOf(JetpackLibsIcons.RESET_SETTINGS) }

    ButtonWithIcon(
        modifier = modifier.width(width = 190.dp),
        jetpackLibsIcons = jetpackLibsIcons,
        onClick = {
            onClick()
            jetpackLibsIcons = JetpackLibsIcons.DONE
        },
        text = if (jetpackLibsIcons == JetpackLibsIcons.DONE) null else text
            ?: stringResource(R.string.reset_text)
    )
}

@Preview
@Composable
private fun ResetButtonPreview() {
    ResetButton(onClick = {})
}