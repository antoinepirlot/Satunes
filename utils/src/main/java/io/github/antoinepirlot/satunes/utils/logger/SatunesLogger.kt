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

package io.github.antoinepirlot.satunes.utils.logger

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import io.github.antoinepirlot.satunes.utils.utils.readTextFromUri
import io.github.antoinepirlot.satunes.utils.utils.writeToUri
import java.io.File
import java.util.logging.ConsoleHandler
import java.util.logging.FileHandler
import java.util.logging.Logger

/**
 * @author Antoine Pirlot on 15/07/2024
 */
class SatunesLogger private constructor(
    name: String?,
    resourceBundleName: String? = null
) : Logger(name, resourceBundleName) {

    companion object {
        var enabled: Boolean = true
        private const val MAX_BYTES = 5242880 // 5MB
        private const val MAX_FILES = 1
        lateinit var DOCUMENTS_PATH: String
        private lateinit var LOGS_PATH: String
        private lateinit var _logger: SatunesLogger

        fun getLogger(): SatunesLogger? {
            if (!enabled) return null
            if (!this::_logger.isInitialized) {
                _logger = SatunesLogger(this::class.java.name)
            }
            return _logger
        }
    }

    init {
        LOGS_PATH = "$DOCUMENTS_PATH/logs"
        addHandler(ConsoleHandler())
        addHandler(SatunesLoggerHandler())
        try {
            File(LOGS_PATH).mkdir()
            val fileHandler = FileHandler(
                "$LOGS_PATH/log",
                MAX_BYTES,
                MAX_FILES,
                true
            )
            fileHandler.formatter = SatunesLoggerFormatter()
            fileHandler.filter = SatunesLoggerFilter()
            addHandler(fileHandler)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @Throws(
        NullPointerException::class,
        SecurityException::class,
        IllegalStateException::class
    )
    fun exportLogs(context: Context, uri: Uri) {
        info("exporting")
        val file = File("$LOGS_PATH/log")
        if (!file.isFile) {
            val message = "$LOGS_PATH is not a file or doesn't exists"
            severe(message)
            throw IllegalStateException(message)
        }
        var text: String = ""
        text +=
            readTextFromUri(context = context, uri = file.toUri()) ?: return
        writeToUri(context = context, uri = uri, string = text)
    }
}