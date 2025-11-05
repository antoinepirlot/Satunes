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

package io.github.antoinepirlot.satunes.ui.components.settings.permissions

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R

private val spacerSize = 16.dp

@Composable
fun Permission(
    modifier: Modifier = Modifier,
    isGranted: Boolean,
    jetpackLibsIcons: JetpackLibsIcons,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = jetpackLibsIcons.imageVector,
            contentDescription = jetpackLibsIcons.description
        )
        Spacer(modifier = Modifier.size(16.dp))
        NormalText(text = title)
        Spacer(modifier = Modifier.size(spacerSize))
        val isGrantedJetpackLibsIcons: JetpackLibsIcons =
            if (isGranted) JetpackLibsIcons.PERMISSION_GRANTED
            else JetpackLibsIcons.PERMISSION_NOT_GRANTED

        Icon(
            imageVector = isGrantedJetpackLibsIcons.imageVector,
            contentDescription = isGrantedJetpackLibsIcons.description,
            tint = if (isGranted) Color.Green else Color.Red
        )
        if (!isGranted) {
            Spacer(modifier = Modifier.size(spacerSize))
            Button(onClick = onClick) {
                NormalText(text = stringResource(id = R.string.ask_permission))
            }
        }
    }
}

@Preview
@Composable
fun PermissionPreview(modifier: Modifier = Modifier) {
    Permission(
        isGranted = true,
        jetpackLibsIcons = JetpackLibsIcons.FOLDER,
        title = "Hello",
        onClick = {})
}