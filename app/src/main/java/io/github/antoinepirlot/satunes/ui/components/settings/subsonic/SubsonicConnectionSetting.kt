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

package io.github.antoinepirlot.satunes.ui.components.settings.subsonic

import android.os.Build
import android.view.View.AUTOFILL_HINT_PASSWORD
import android.view.View.AUTOFILL_HINT_USERNAME
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.LoadingCircle
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SubsonicUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SubsonicViewModel
import io.github.antoinepirlot.satunes.database.models.User
import io.github.antoinepirlot.satunes.internet.subsonic.models.SubsonicState
import io.github.antoinepirlot.satunes.ui.components.settings.SubSettings
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot 03/09/2025
 */

@Composable
fun SubsonicConnectionSetting(
    modifier: Modifier = Modifier,
) {
    SubSettings(
        modifier = modifier,
        title = stringResource(R.string.subsonic_connection_title),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NormalText(text = stringResource(R.string.subsonic_connection_text), maxLines = 2)
        Form()
        Buttons()
    }
}

@Composable
fun Form(
    modifier: Modifier = Modifier,
    subsonicViewModel: SubsonicViewModel = viewModel()
) {
    val user: User = subsonicViewModel.user
    Column(modifier = Modifier) {
        OutlinedTextField(
            value = user.url,
            onValueChange = { subsonicViewModel.updateSubsonicUrl(url = it) },
            label = { NormalText(text = "URL") },
            placeholder = { NormalText(text = "https://example.org") },
            singleLine = true
        )
        OutlinedTextField(
            modifier = Modifier.semantics {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    contentType = ContentType(AUTOFILL_HINT_USERNAME)
                }
            },
            value = user.username,
            onValueChange = { subsonicViewModel.updateSubsonicUsername(username = it) },
            label = { NormalText(text = "Username") },
            placeholder = { NormalText(text = "username") },
            singleLine = true
        )
        OutlinedTextField(
            modifier = Modifier.semantics {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    contentType = ContentType(AUTOFILL_HINT_PASSWORD)
                }
            },
            value = user.password,
            onValueChange = { subsonicViewModel.updateSubsonicPassword(password = it) },
            label = { NormalText(text = "Password") },
            placeholder = { NormalText(text = "@Password123") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = user.salt,
            onValueChange = { subsonicViewModel.updateSubsonicSalt(salt = it) },
            label = { NormalText(text = stringResource(id = R.string.salt_label)) },
            placeholder = { NormalText(text = stringResource(id = R.string.salt_placeholder)) },
            singleLine = true,
        )
    }
}

/**
 * Confirm and cancel button
 */
@Composable
private fun Buttons(
    modifier: Modifier = Modifier,
    subsonicViewModel: SubsonicViewModel = viewModel()
) {
    val subsonicUiState: SubsonicUiState by subsonicViewModel.uiState.collectAsState()
    val scope: CoroutineScope = LocalMainScope.current
    val snackbarHostState: SnackbarHostState = LocalSnackBarHostState.current

    val canTryConnection: Boolean = subsonicUiState.subsonicState in listOf(
        SubsonicState.DISCONNECTED, SubsonicState.IDLE, SubsonicState.ERROR
    )

    Row(modifier = modifier) {
        if (subsonicViewModel.hasBeenUpdated) {
            Button(onClick = { subsonicViewModel.reset() }) {
                NormalText(text = stringResource(R.string.cancel))
            }

            Spacer(modifier = Modifier.size(size = 16.dp))
        }

        Button(
            onClick = {
                subsonicViewModel.testConnection(
                    scope = scope,
                    snackbarHostState = snackbarHostState
                )
            },
            enabled = canTryConnection
        ) {
            if (subsonicUiState.subsonicState == SubsonicState.PINGING)
                LoadingCircle(modifier = Modifier.size(size = 16.dp))
            else
                NormalText(text = stringResource(R.string.test_connection_button_text))
        }
    }
}

@Preview
@Composable
private fun SubsonicSettingsViewPreview() {
    SubsonicConnectionSetting()
}