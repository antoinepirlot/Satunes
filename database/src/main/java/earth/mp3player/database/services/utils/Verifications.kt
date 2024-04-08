/*
 * This file is part of MP3 Player.
 *
 * MP3 Player is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MP3 Player.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * **** INFORMATIONS ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 * You can contact me via my email: pirlot.antoine@outlook.com
 * PS: I don't answer quickly.
 */

package earth.mp3player.database.services.utils

import android.content.Context
import earth.mp3player.database.R

/**
 * @author Antoine Pirlot on 07/04/2024
 */

private fun computeString(context: Context, text: String, isPath: Boolean = false): String {
    return if (isPath) {
        if (text.isBlank()) {
            throw IllegalArgumentException("The relative path is blank")
        }
        escape(text = text, isPath = true)
    } else {
        if (text.isBlank()) {
            context.resources.getString(R.string.unknown)
        } else {
            escape(text = text)
        }
    }
}

private fun escape(text: String, isPath: Boolean = false): String {
    val regex = Regex("[^\\w_.-]") // Matches characters except what's inside
    val regexPath = Regex("[^\\w_./\\\\-]") // Matches characters except what's inside
    return text.replace(if (isPath) regexPath else regex) { matchResult ->
        var escapedChars = ""
        matchResult.value.toCharArray().forEach { c: Char ->
            val hexaCode: String = c.code.toString(16)
            escapedChars += if (c.code > 127) {  // Check for characters above ASCII range
                // Encode Unicode character using toHexString
                "\\u${hexaCode.padStart(4, '0')}"
            } else {
                "%${hexaCode}"
            }
        }
        escapedChars
    }
}

private fun unescape(text: String): String {
    val regex = Regex("(%[0-9a-fA-F]{2})|(\\\\u[0-9a-fA-F]{4})")
    return text.replace(regex) { matchResult ->
        var unescapedText = ""
        val s: String = matchResult.value
        if (s.startsWith("\\u")) {
            // Decode Unicode escape sequence
            unescapedText += decodeUnicode(text = s)
        } else if (s.startsWith("%")) {
            // Remove escaped character
            unescapedText += decodeAscii(text = s)
        }
        unescapedText
    }
}

/**
 * Decode Ascii String (format: %xy) (hexadecimal)
 */
private fun decodeAscii(text: String): String {
    var decoded = ""
    val splitText: List<String> = text.split("%")
    splitText.forEach { s: String ->
        if (s.isNotBlank()) {
            // Extract hex code
            decoded += s.toInt(16).toChar().toString()
        }
    }
    return decoded
}

private fun decodeUnicode(text: String): String {
    var decoded = ""
    text.split("\\u").forEach { s: String ->
        if (s.isNotBlank()) {
            decoded += s.toInt(16).toChar().toString()
        }
    }
    return decoded
}


