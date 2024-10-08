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

package io.github.antoinepirlot.satunes.widgets

import android.content.Context
import android.os.Environment
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.playback.services.WidgetPlaybackManager
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import io.github.antoinepirlot.satunes.widgets.ui.views.classic_playback.ClassicPlaybackWidgetView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 20/08/2024
 */

class ClassicPlaybackWidget : GlanceAppWidget() {

    private lateinit var _logger: SatunesLogger

    companion object {
        fun setRefreshWidget(context: Context) {
            val refreshWidgets: () -> Unit = {
                CoroutineScope(Dispatchers.Default).launch {
                    ClassicPlaybackWidget().updateAll(context = context.applicationContext)
                }
            }
            WidgetPlaybackManager.setRefreshWidgets(refreshWidgets = refreshWidgets)
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        SatunesLogger.DOCUMENTS_PATH =
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.path
        _logger = SatunesLogger.getLogger()
        _logger.info("ClassicPlaybackWidget Starting")
        setRefreshWidget(context = context)
        WidgetPlaybackManager.refreshWidgets()

        provideContent {
            GlanceTheme {
                Scaffold(
                    modifier = GlanceModifier.clickable(onClick = actionStartActivity<MainActivity>())
                ) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ClassicPlaybackWidgetView()
                    }
                }
            }

        }
    }
}