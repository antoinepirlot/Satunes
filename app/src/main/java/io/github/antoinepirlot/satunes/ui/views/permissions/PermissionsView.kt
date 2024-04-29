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

package io.github.antoinepirlot.satunes.ui.views.permissions

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.icons.SatunesIcons
import io.github.antoinepirlot.satunes.services.PermissionManager
import io.github.antoinepirlot.satunes.services.Permissions
import io.github.antoinepirlot.satunes.services.permissionsList
import io.github.antoinepirlot.satunes.ui.components.texts.NormalText
import io.github.antoinepirlot.satunes.ui.components.texts.Title

/**
 * @author Antoine Pirlot on 29/04/2024
 */

@Composable
fun PermissionsView(
    modifier: Modifier = Modifier,
) {
    val spacerSize = 16.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Title(text = stringResource(id = R.string.permissions))
        NormalText(
            modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.no_required_permissions_allowed),
            maxLines = 2
        )
        Spacer(modifier = Modifier.size(spacerSize))
        val lazySate = rememberLazyListState()
        LazyColumn(
            state = lazySate,
        ) {
            items(
                items = permissionsList,
                key = { it.stringId }
            ) { permission: Permissions ->
                if (
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && permission != Permissions.READ_EXTERNAL_STORAGE)
                    || (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && permission != Permissions.READ_AUDIO_PERMISSION)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NormalText(text = stringResource(id = permission.stringId))
                        Spacer(modifier = Modifier.size(spacerSize))
                        val permissionGranted = when (permission) {
                            Permissions.READ_AUDIO_PERMISSION -> PermissionManager.isReadAudioAllowed
                            Permissions.READ_EXTERNAL_STORAGE -> PermissionManager.isReadExternalStorageAllowed
                        }

                        if (permissionGranted) {
                            val icon: SatunesIcons = SatunesIcons.PERMISSION_GRANTED
                            Icon(
                                imageVector = icon.imageVector,
                                contentDescription = icon.description,
                                tint = Color.Green
                            )
                        } else {
                            val icon: SatunesIcons = SatunesIcons.PERMISSION_NOT_GRANTED
                            Icon(
                                imageVector = icon.imageVector,
                                contentDescription = icon.description,
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.size(spacerSize))
                            Button(onClick = {
                                when (permission) {
                                    Permissions.READ_AUDIO_PERMISSION -> TODO()

                                    Permissions.READ_EXTERNAL_STORAGE -> TODO()
                                }
                            }) {
                                Text(text = stringResource(id = R.string.ask_permission))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PermissionsViewPreview() {
    PermissionsView()
}