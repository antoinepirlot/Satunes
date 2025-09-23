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

package io.github.antoinepirlot.satunes.ui.views.settings

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.models.SettingsViews
import io.github.antoinepirlot.satunes.ui.components.buttons.settings.SettingButton
import io.github.antoinepirlot.satunes.ui.components.settings.about.AboutSettings

/**
 * @author Antoine Pirlot on 02/03/24
 */

@Composable
internal fun SettingsView(
    modifier: Modifier = Modifier,
) {
    val navController: NavHostController = LocalNavController.current
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(state = scrollState)) {
        Title(text = stringResource(id = R.string.settings))
        HorizontalDivider()
        Column {
            for (settingView: SettingsViews in SettingsViews.entries)
                if (Build.VERSION.SDK_INT >= settingView.minSdk)
                    SettingButton(
                        text = stringResource(settingView.stringId),
                        icon = settingView.icon,
                        onClick = {
                            navController.navigate(settingView.destination.link)
                        }
                    )

            AboutSettings(modifier.padding(bottom = 16.dp)) // Bottom padding for a little space
        }
    }
}

@Composable
@Preview
private fun SettingsViewPreview() {
    SettingsView()
}