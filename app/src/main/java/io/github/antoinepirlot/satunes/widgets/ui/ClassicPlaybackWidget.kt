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

package io.github.antoinepirlot.satunes.widgets.ui

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
import io.github.antoinepirlot.satunes.MainActivity
import io.github.antoinepirlot.satunes.utils.logger.Logger
import io.github.antoinepirlot.satunes.widgets.ui.views.ClassicPlaybackWidgetView
import io.github.antoinepirlot.satunes.widgets.ui.views.WidgetView

/**
 * @author Antoine Pirlot on 20/08/2024
 */

class ClassicPlaybackWidget : GlanceAppWidget() {

    private var _logger: Logger? = null

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Logger.Companion.DOCUMENTS_PATH =
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.path
        _logger = Logger.Companion.getLogger()
        _logger?.info("ClassicPlaybackWidget Starting")

        provideContent {
            GlanceTheme {
                Scaffold(
                    modifier = GlanceModifier.clickable(onClick = actionStartActivity<MainActivity>())
                ) {
                    WidgetView {
                        ClassicPlaybackWidgetView()
                    }
                }
            }
        }
    }
}