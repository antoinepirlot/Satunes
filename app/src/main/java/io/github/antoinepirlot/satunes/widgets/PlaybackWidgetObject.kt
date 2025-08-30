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

package io.github.antoinepirlot.satunes.widgets

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import io.github.antoinepirlot.satunes.playback.services.WidgetPlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot 20/04/2025
 */
object PlaybackWidget {
    private val _widgets: MutableMap<GlanceId, GlanceAppWidget> = mutableMapOf()

    fun addWidget(context: Context, glanceId: GlanceId, widget: GlanceAppWidget) {
        if (this._widgets.contains(key = glanceId)) return
        this._widgets[glanceId] = widget
        setRefreshWidget(context = context)
        WidgetPlaybackManager.refreshWidgets()
    }

    fun setRefreshWidget(context: Context) {
        if (this._widgets.isEmpty())
            WidgetPlaybackManager.setRefreshWidgets(refreshWidgets = null)
        else {
            val refreshWidgets: () -> Unit = {
                CoroutineScope(Dispatchers.Default).launch {
                    for (entry: Map.Entry<GlanceId, GlanceAppWidget> in _widgets.entries)
                        entry.value.update(context = context.applicationContext, id = entry.key)
                }
            }
            WidgetPlaybackManager.setRefreshWidgets(refreshWidgets = refreshWidgets)
        }
    }

    fun removeWidget(context: Context, glanceId: GlanceId) {
        this._widgets.remove(key = glanceId)
        setRefreshWidget(context = context)
    }
}