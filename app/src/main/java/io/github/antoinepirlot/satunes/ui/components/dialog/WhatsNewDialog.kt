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

package io.github.antoinepirlot.satunes.ui.components.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.antoinepirlot.jetpack_libs.components.JetpackLibsIcons
import io.github.antoinepirlot.jetpack_libs.components.texts.NormalText
import io.github.antoinepirlot.jetpack_libs.components.texts.Title
import io.github.antoinepirlot.satunes.R
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.internet.updates.Versions.TAG_RELEASE_URL
import io.github.antoinepirlot.satunes.ui.utils.openUrl
import kotlinx.coroutines.CoroutineScope
import io.github.antoinepirlot.satunes.internet.R as RInternet

/**
 * @author Antoine Pirlot on 29/06/2024
 */

@SuppressLint("NewApi")
@Composable
internal fun WhatsNewDialog(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel(),
) {
    val context: Context = LocalContext.current
    val scope: CoroutineScope = LocalMainScope.current
    val snackBarHostState: SnackbarHostState = LocalSnackBarHostState.current
    val packageManager = context.packageManager
    val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
    val versionName = 'v' + packageInfo.versionName!!

    val onConfirm: () -> Unit = {
        // When app relaunch, it's not shown again
        satunesViewModel.seeWhatsNew(
            scope = scope,
            snackBarHostState = snackBarHostState,
            permanently = true
        )

    }
    val onDismiss: () -> Unit = {
        // When app relaunch, it's shown again
        satunesViewModel.seeWhatsNew(
            scope = scope,
            snackBarHostState = snackBarHostState,
        )
    }

    AlertDialog(
        modifier = modifier,
        icon = {
            val infoJetpackLibsIcons: JetpackLibsIcons = JetpackLibsIcons.INFO
            Icon(
                imageVector = infoJetpackLibsIcons.imageVector,
                contentDescription = infoJetpackLibsIcons.description
            )
        },
        title = {
            Title(text = versionName, fontSize = 25.sp)
        },
        text = {
            val scrollState = rememberScrollState()
            NormalText(
                modifier = Modifier.verticalScroll(scrollState),
                text = stringResource(id = R.string.whats_new_text),
                maxLines = Int.MAX_VALUE
            )
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onConfirm) {
                NormalText(text = stringResource(id = R.string.ok))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                openUrl(
                    context = context,
                    url = "$TAG_RELEASE_URL/$versionName"
                )
            }) {
                NormalText(text = stringResource(id = RInternet.string.see_on_codeberg))
            }
        },
    )
}

@Preview
@Composable
private fun WhatsNewDialogPreview() {
    WhatsNewDialog()
}