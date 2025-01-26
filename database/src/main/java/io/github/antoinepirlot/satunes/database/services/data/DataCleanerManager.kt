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
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.services.data

import android.content.Context
import android.os.Environment
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import java.io.File

/**
 * @author Antoine Pirlot on 14/04/2024
 */
object DataCleanerManager {
    private val logger = SatunesLogger.getLogger()

    fun removeApkFiles(context: Context) {
        try {
            val downloadDirectory: File? =
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            downloadDirectory?.listFiles()?.forEach { file: File ->
                file.deleteRecursively()
            }
        } catch (e: Throwable) {
            logger.warning(e.message)
            e.printStackTrace()
        }
    }
}