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

package io.github.antoinepirlot.satunes.models

import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.Timer
import java.util.TimerTask

/**
 * @author Antoine Pirlot on 13/10/2024
 */
class Timer(
    function: () -> Unit,
    hours: Int,
    minutes: Int,
    seconds: Int
) {
    private val _logger: SatunesLogger = SatunesLogger.getLogger()
    private val _task: TimerTask = Task(function = function)
    private val _delayMillis: Long =
        (hours.toLong() * 3600L + minutes.toLong() * 60L + seconds.toLong()) * 1000L
    private val _createdTimeMillis: Long = System.currentTimeMillis()
    private val _timer = Timer()

    init {
        _logger.info("Create Timer")
        if (minutes < 0 || seconds < 0)
            throw IllegalArgumentException("Minutes is: $minutes and seconds is: $seconds.")
        _timer.schedule(_task, _delayMillis)
    }

    fun cancel() {
        _logger.info("Cancel Timer")
        _timer.cancel()
    }

    fun getRemainingTime(): Long {
        return _delayMillis - (System.currentTimeMillis() - _createdTimeMillis)
    }

    private inner class Task(private val function: () -> Unit) : TimerTask() {
        override fun run() {
            _logger.info("Run Timer Task")
            runBlocking(Dispatchers.Main) {
                function()
            }
        }
    }
}