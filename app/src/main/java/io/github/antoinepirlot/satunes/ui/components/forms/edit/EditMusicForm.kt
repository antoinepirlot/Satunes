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

package io.github.antoinepirlot.satunes.ui.components.forms.edit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.DataViewModel
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.ui.components.forms.CancelButton
import io.github.antoinepirlot.satunes.ui.components.forms.ConfirmButton
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 12/09/2024
 */

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
internal fun EditMusicForm(
    modifier: Modifier = Modifier,
    dataViewModel: DataViewModel = viewModel(),
    music: Music,
) {
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    val updatedMusic: Music by remember { mutableStateOf(music.clone()) }
    var placeholder: String by remember { mutableStateOf(updatedMusic.title) }

    Column(modifier = modifier) {
        EditRow(
            value = updatedMusic.title,
            onValueChange = {
                updatedMusic.title = it
            },
            placeholder = placeholder,
            label = stringResource(R.string.title)
        )

        Row {
            CancelButton(onCancel = { updatedMusic.title = music.title })
            Spacer(Modifier.size(8.dp))
            ConfirmButton(
                onConfirm = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        dataViewModel.updateMusic(
                            scope = scope,
                            snackBarHostState = snackBarHostState,
                            updatedMusic = updatedMusic,
                            onFinish = { placeholder = music.title }
                        )
                    }
                }
            )
        }
    }
}