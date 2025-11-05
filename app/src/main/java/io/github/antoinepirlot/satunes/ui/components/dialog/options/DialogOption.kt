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

package io.github.antoinepirlot.satunes.ui.components.dialog.options

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.components.LoadingCircle

/**
 * @author Antoine Pirlot on 19/04/2024
 */

private val SPACER_SIZE = 10.dp

@Composable
internal fun DialogOption(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    jetpackLibsIcons: JetpackLibsIcons,
    text: String,
    isLoading: Boolean = false
) {
    TextButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading)
                LoadingCircle(modifier = Modifier.size(20.dp))
            else
                Icon(
                    imageVector = jetpackLibsIcons.imageVector,
                    contentDescription = jetpackLibsIcons.description
                )
            Spacer(modifier = Modifier.size(SPACER_SIZE))
            NormalText(text = text)
        }
    }
}

@Preview
@Composable
private fun DialogOptionPreview() {
    DialogOption(
        onClick = {},
        jetpackLibsIcons = JetpackLibsIcons.ADD,
        text = "Dialog Option"
    )
}