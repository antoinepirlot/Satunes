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

package io.github.antoinepirlot.satunes.widgets.receivers

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import io.github.antoinepirlot.satunes.widgets.PlaybackWidget
import kotlinx.coroutines.runBlocking

/**
 * @author Antoine Pirlot 24/04/2025
 */
open class WidgetReceiver(override val glanceAppWidget: GlanceAppWidget) :
    GlanceAppWidgetReceiver() {

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        this.addWidget(context = context)
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        this.addWidget(context = context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        this.removeWidgets(context = context, appWidgetIds = appWidgetIds)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        this.addWidgets(context = context, appWidgetIds = appWidgetIds)
    }

    private fun addWidgets(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId: Int in appWidgetIds)
            this.addWidget(context = context, appWidgetId = appWidgetId)
    }

    private fun addWidget(context: Context?, appWidgetId: Int? = null) {
        if (context == null) return
        runBlocking {
            val appWidgetManager = GlanceAppWidgetManager(context = context.applicationContext)
            if (appWidgetId == null) {
                val glanceIds: List<GlanceId>? =
                    appWidgetManager.getGlanceIds(glanceAppWidget::class.java)
                if (glanceIds == null) return@runBlocking
                for (glanceId: GlanceId in glanceIds)
                    PlaybackWidget.addWidget(
                        context = context,
                        glanceId = glanceId,
                        widget = glanceAppWidget
                    )
            } else {
                val glanceId: GlanceId = appWidgetManager.getGlanceIdBy(appWidgetId = appWidgetId)
                //Use the same widget for all ids because it seems it's only one instance for all ids.
                //The enabled is not called starting the second same widget creation.
                PlaybackWidget.addWidget(
                    context = context,
                    glanceId = glanceId,
                    widget = glanceAppWidget
                )
            }
        }
    }

    private fun removeWidgets(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId: Int in appWidgetIds) {
            val glanceId: GlanceId =
                GlanceAppWidgetManager(context = context).getGlanceIdBy(appWidgetId)
            PlaybackWidget.removeWidget(context = context, glanceId = glanceId)
        }
    }
}