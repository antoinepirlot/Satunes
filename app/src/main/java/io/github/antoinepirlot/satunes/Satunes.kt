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

package io.github.antoinepirlot.satunes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.antoinepirlot.satunes.data.local.LocalMainScope
import io.github.antoinepirlot.satunes.data.local.LocalNavController
import io.github.antoinepirlot.satunes.data.local.LocalSnackBarHostState
import io.github.antoinepirlot.satunes.data.states.SatunesUiState
import io.github.antoinepirlot.satunes.data.viewmodels.SatunesViewModel
import io.github.antoinepirlot.satunes.router.Router
import io.github.antoinepirlot.satunes.ui.components.bars.bottom.BottomAppBar
import io.github.antoinepirlot.satunes.ui.components.bars.top.TopAppBar
import io.github.antoinepirlot.satunes.ui.components.buttons.fab.SatunesFAB
import io.github.antoinepirlot.satunes.ui.components.dialog.WhatsNewDialog
import io.github.antoinepirlot.satunes.ui.theme.SatunesTheme
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.CoroutineScope

/**
 * @author Antoine Pirlot on 10/04/2024
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Satunes(
    modifier: Modifier = Modifier,
    satunesViewModel: SatunesViewModel = viewModel()
) {
    SatunesLogger.getLogger()?.info("Satunes Composable")
    val satunesUiState: SatunesUiState by satunesViewModel.uiState.collectAsState()
    SatunesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior =
                TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            val navController: NavHostController = rememberNavController()
            val scope: CoroutineScope = rememberCoroutineScope()
            val snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }

            CompositionLocalProvider(
                values = arrayOf(
                    LocalSnackBarHostState provides snackBarHostState,
                    LocalMainScope provides scope,
                    LocalNavController provides navController,
                )
            ) {
                Scaffold(
                    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = { TopAppBar(scrollBehavior = scrollBehavior) },
                    bottomBar = { BottomAppBar() },
                    floatingActionButton = { SatunesFAB() },
                    floatingActionButtonPosition = FabPosition.End
                ) { innerPadding: PaddingValues ->
                    Router(modifier = Modifier.padding(innerPadding))
                    if (!satunesUiState.whatsNewSeen) {
                        WhatsNewDialog(
                            onConfirm = {
                                // When app relaunch, it's not shown again
                                satunesViewModel.seeWhatsNew(
                                    scope = scope,
                                    snackBarHostState = snackBarHostState,
                                    permanently = true
                                )

                            },
                            onDismiss = {
                                // When app relaunch, it's shown again
                                satunesViewModel.seeWhatsNew(
                                    scope = scope,
                                    snackBarHostState = snackBarHostState,
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ApplicationPreview() {
    Satunes()
}