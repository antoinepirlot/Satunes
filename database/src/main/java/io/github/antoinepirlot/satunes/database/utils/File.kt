package io.github.antoinepirlot.satunes.database.utils

import io.github.antoinepirlot.android.utils.utils.lastIndex
import java.io.File
import java.io.IOException

/**
 * @author Antoine Pirlot 23/12/2025
 */

/**
 * Creates all folder then the final file if not exists.
 *
 * @param path the [String] path from where to start creation
 *
 * @throws IOException if an [IOException] is thrown.
 * @throws SecurityException if an [SecurityException] is thrown.
 *
 * @return the created [File]
 */
internal fun createFile(path: String): File {
    val fileList: MutableList<String> = path.split("/").toMutableList()
    val fileName: String = fileList.removeAt(index = fileList.lastIndex)
    val path: String = path.removeSuffix(suffix = "/$fileName")
    File(path).mkdirs()
    val newFile: File = File("$path/$fileName")
    if (newFile.exists()) newFile.delete()
    newFile.createNewFile()
    return newFile
}