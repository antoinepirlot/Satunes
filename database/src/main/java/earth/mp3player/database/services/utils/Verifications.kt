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

internal fun computeString(context: Context, string: String, isPath: Boolean = false): String {
    return if (isPath) {
        if (string.isBlank()) {
            throw IllegalArgumentException("The relative path is blank")
        }
        escape(text = string, isPath = true)
    } else {
        if (string.isBlank()) {
            context.resources.getString(R.string.unknown)
        } else {
            escape(text = string)
        }
    }
}

private fun escape(text: String, isPath: Boolean = false): String {
    val regex = Regex("[^\\w_.-]") // Matches characters except what's inside
    val regexPath = Regex("[^\\w_./\\-]") // Matches characters except what's inside
    return text.replace(if (isPath) regexPath else regex) { matchResult: MatchResult ->
        // Encode each matched character
        val code: String = matchResult.value.toCharArray()[0].code.toUInt().toString(16)
        "%$code"
    }
}

fun unescape(text: String): String {
    val regex = Regex("%[0-9A-Fa-f]{2}") // Matches "%XX" format (hexadecimal)
    return text.replace(regex) { matchResult: MatchResult ->
        val code = matchResult.value.substring(1, 3) // Extract hex code
        code.toInt(16).toChar().toString() // Convert hex code to character
    }
}


