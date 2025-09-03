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

package io.github.antoinepirlot.satunes.internet.subsonic.callbacks

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicState
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * @author Antoine Pirlot 03/09/2025
 */

@RequiresApi(Build.VERSION_CODES.M)
abstract class SubsonicCallback(
    protected var subsonicApiRequester: SubsonicApiRequester
) : Callback {

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
        SubsonicState.ERROR.code = null
        subsonicApiRequester.subsonicState = SubsonicState.ERROR
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.code >= 400) {
            SubsonicState.ERROR.code = response.code
            subsonicApiRequester.subsonicState = SubsonicState.ERROR
        }
    }
}