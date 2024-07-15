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

package io.github.antoinepirlot.satunes.logger

import java.io.IOException
import java.util.logging.ConsoleHandler
import java.util.logging.FileHandler
import java.util.logging.Logger

/**
 * @author Antoine Pirlot on 15/07/2024
 */
class SatunesLogger(
    name: String?,
    resourceBundleName: String? = null
) : Logger(name, resourceBundleName) {

    companion object {
        private const val MAX_LINES = 1000
        private const val MAX_FILES = 5
        lateinit var DOCUMENTS_PATH: String
    }

    init {
        addHandler(ConsoleHandler())
        addHandler(SatunesLoggerHandler())
        try {
            val fileHandler = FileHandler(
                "$DOCUMENTS_PATH/logs",
                MAX_LINES,
                MAX_FILES
            )
            fileHandler.formatter = SatunesLoggerFormatter()
            fileHandler.filter = SatunesLoggerFilter()
            addHandler(fileHandler)
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}