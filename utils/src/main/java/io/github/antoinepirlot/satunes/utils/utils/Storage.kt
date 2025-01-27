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

package io.github.antoinepirlot.satunes.utils.utils

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import io.github.antoinepirlot.satunes.utils.R
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author Antoine Pirlot on 16/07/2024
 */

/**
 * Copied from https://developer.android.com/training/data-storage/shared/documents-files?hl=fr#open
 */
@Throws(IOException::class)
fun readTextFromUri(context: Context, uri: Uri, showToast: Boolean = false): String? {
    val logger: SatunesLogger? = SatunesLogger.getLogger()
    return try {
        val stringBuilder = StringBuilder()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        stringBuilder.toString()
    } catch (e: FileNotFoundException) {
        logger?.warning(e.message)
        showToastOnUiThread(context = context, message = context.getString(R.string.file_not_found))
        e.printStackTrace()
        null
    } catch (e: SecurityException) {
        logger?.warning(e.message)
        showToastOnUiThread(
            context = context,
            message = context.getString(R.string.file_security_exception)
        )
        e.printStackTrace()
        null
    } catch (e: Throwable) {
        logger?.severe(e.message)
        throw e
    }
}

/**
 * Copied from https://developer.android.com/training/data-storage/shared/documents-files?hl=fr#edit
 */
fun writeToUri(context: Context, uri: Uri, string: String): Boolean {
    val logger = SatunesLogger.getLogger()
    return try {
        context.contentResolver.openFileDescriptor(uri, "w")
            ?.use { parcelFileDescriptor: ParcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use { fileOutputStream: FileOutputStream ->
                    fileOutputStream.write((string).toByteArray())
                }
            }
        true
    } catch (e: FileNotFoundException) {
        logger?.warning(e.message)
        showToastOnUiThread(context = context, message = context.getString(R.string.file_not_found))
        e.printStackTrace()
        false
    } catch (e: SecurityException) {
        logger?.warning(e.message)
        showToastOnUiThread(
            context = context,
            message = context.getString(R.string.file_security_exception)
        )
        e.printStackTrace()
        false
    } catch (e: Throwable) {
        logger?.severe(e.message)
        throw e
    }
}