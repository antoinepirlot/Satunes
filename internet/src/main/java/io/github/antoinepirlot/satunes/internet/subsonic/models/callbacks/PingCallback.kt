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
 * You should have received a copy of the GNU General Public License along with Satunes.
 *
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on Codeberg.
 *
 * My Codeberg link is: https://codeberg.org/antoinepirlot
 * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
 *
 */

package io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks

import android.os.Build
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.SubsonicResponse
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot 03/09/2025
 */

@RequiresApi(Build.VERSION_CODES.M)
internal class PingCallback(
    subsonicApiRequester: SubsonicApiRequester,
    onSucceed: (() -> Unit)? = null
) : SubsonicCallback(
    subsonicApiRequester = subsonicApiRequester,
    onSucceed = onSucceed
) {

    private val _logger: Logger? = Logger.getLogger()

    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call = call, response = response)
        if(!this.hasReceivedData()) return
        val response: SubsonicResponse = this.getSubsonicResponse()
        SubsonicApiRequester.status = response.status
        subsonicApiRequester.updateVersion(version = response.version)
        SubsonicApiRequester.type = response.type
        SubsonicApiRequester.serverVersion = response.serverVersion
        SubsonicApiRequester.openSubsonic = response.openSubsonic

        this.dataProcessed()
        this.onSucceed?.invoke()
    }
}