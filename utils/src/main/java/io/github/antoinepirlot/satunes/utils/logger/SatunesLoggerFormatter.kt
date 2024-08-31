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

package io.github.antoinepirlot.satunes.utils.logger

import java.util.Date
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord

/**
 * @author Antoine Pirlot on 15/07/2024
 */
internal class SatunesLoggerFormatter : Formatter() {

    override fun format(record: LogRecord?): String {
        record ?: return "Log record is null"
        val threadId: Int = record.threadID
        val sourceClassName: String = record.sourceClassName
        val sourceMethodName: String? = record.sourceMethodName
        val date = Date(record.millis)
        val message: String = record.message
        val level: Level = record.level

        return "$level::$threadId::$sourceClassName::$sourceMethodName::$date::$message ||${System.lineSeparator()}"
    }
}