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
import io.github.antoinepirlot.satunes.internet.subsonic.models.SubsonicErrorCode
import io.github.antoinepirlot.satunes.internet.subsonic.models.SubsonicState
import io.github.antoinepirlot.satunes.internet.subsonic.models.XmlObject
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import okhttp3.Call
import okhttp3.Response

/**
 * @author Antoine Pirlot 03/09/2025
 */

@RequiresApi(Build.VERSION_CODES.M)
internal class PingCallback(
    subsonicApiRequester: SubsonicApiRequester
) : SubsonicCallback(subsonicApiRequester = subsonicApiRequester) {
    private val _logger: SatunesLogger? = SatunesLogger.getLogger()

    override fun onResponse(call: Call, response: Response) {
        super.onResponse(call = call, response = response)
        if (subsonicApiRequester.subsonicState == SubsonicState.ERROR) return
        if (subsonicApiRequester.subsonicState != SubsonicState.DATA_RECEIVED) {
            setUnknownError()
            return
        }
        val xmlObject: XmlObject? = subsonicApiRequester.subsonicState.dataReceived
        if (xmlObject == null) {
            setUnknownError()
            return
        }
        SubsonicApiRequester.status = xmlObject.status
        SubsonicApiRequester.version = xmlObject.version
        SubsonicApiRequester.type = xmlObject.type
        SubsonicApiRequester.serverVersion = xmlObject.serverVersion
        SubsonicApiRequester.openSubsonic = xmlObject.openSubsonic

        subsonicApiRequester.subsonicState = SubsonicState.DISCONNECTED
    }

    private fun setUnknownError() {
        _logger?.warning("Unknown error while fetching ping data.")
        subsonicApiRequester.subsonicState = SubsonicState.ERROR
        SubsonicState.ERROR.error = SubsonicErrorCode.UNKNOWN
    }
}