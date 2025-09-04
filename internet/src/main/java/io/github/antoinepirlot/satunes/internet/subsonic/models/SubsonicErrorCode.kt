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

package io.github.antoinepirlot.satunes.internet.subsonic.models

import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author Antoine Pirlot 04/09/2025
 */

@RequiresApi(Build.VERSION_CODES.M)
enum class SubsonicErrorCode(val code: Int, val message: String) {
    GENERIC_ERROR(code = 0, message = "A generic error."),
    MISSING_PARAMETERS(code = 10, message = "Required parameter is missing."),
    CLIENT_MUST_UPGRADE(
        code = 20,
        message = "Incompatible Subsonic REST protocol version. Client must upgrade."
    ),
    SERVER_MUST_UPGRADE(
        code = 30,
        message = "Incompatible Subsonic REST protocol version. Server must upgrade."
    ),
    WRONG_CREDENTIALS(code = 40, message = "Wrong username or password."),
    TOKEN_NOT_SUPPORTED(code = 41, message = "Token authentication not supported for LDAP users."),
    NOT_AUTHORIZED(code = 50, message = "User is not authorized for the given operation."),
    TRIAL_UPDATE_FINISHED(
        code = 60,
        message = "The trial period for the Subsonic server is over. Please upgrade to Subsonic Premium. Visit subsonic.org for details. "
    ),
    DATA_NOT_FOUND(code = 70, message = "The requested data was not found."),
    UNKNOWN(code = -1, message = "Unknown error");

    companion object {
        private val codeMap: Map<Int, SubsonicErrorCode> = mapOf(
            Pair<Int, SubsonicErrorCode>(0, GENERIC_ERROR),
            Pair<Int, SubsonicErrorCode>(10, MISSING_PARAMETERS),
            Pair<Int, SubsonicErrorCode>(20, CLIENT_MUST_UPGRADE),
            Pair<Int, SubsonicErrorCode>(30, SERVER_MUST_UPGRADE),
            Pair<Int, SubsonicErrorCode>(40, WRONG_CREDENTIALS),
            Pair<Int, SubsonicErrorCode>(41, TOKEN_NOT_SUPPORTED),
            Pair<Int, SubsonicErrorCode>(50, NOT_AUTHORIZED),
            Pair<Int, SubsonicErrorCode>(60, TRIAL_UPDATE_FINISHED),
            Pair<Int, SubsonicErrorCode>(70, DATA_NOT_FOUND),
        )

        fun getError(code: Int): SubsonicErrorCode {
            if (!codeMap.containsKey(key = code)) return UNKNOWN
            return codeMap[code]!!
        }
    }
}