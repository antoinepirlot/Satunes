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
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlObject
import io.github.antoinepirlot.satunes.internet.subsonic.utils.SubsonicXmlParser
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.io.InputStream


/**
 * @author Antoine Pirlot 03/09/2025
 */

@RequiresApi(Build.VERSION_CODES.M)
abstract class SubsonicCallback(
    protected val subsonicApiRequester: SubsonicApiRequester,
    protected val onSucceed: (() -> Unit)? //Used in children classes
) : Callback {

    private val _logger: SatunesLogger? = SatunesLogger.getLogger()

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
        SubsonicState.ERROR.error = null
        subsonicApiRequester.subsonicState = SubsonicState.ERROR
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.code >= 400) {
            SubsonicState.ERROR.error = SubsonicErrorCode.GENERIC_ERROR
            subsonicApiRequester.subsonicState = SubsonicState.ERROR
            return
        }
        try {
            val input: InputStream = response.body!!.byteStream()
            try {
                val result: List<XmlObject> = SubsonicXmlParser(subsonicApiRequester = subsonicApiRequester).parse(inputStream = input)
                if (result.isEmpty()) {
                    SubsonicState.ERROR.error = SubsonicErrorCode.DATA_NOT_FOUND
                    subsonicApiRequester.subsonicState = SubsonicState.ERROR
                }
                //Maybe error
                if (subsonicApiRequester.subsonicState != SubsonicState.PINGING)
                    for (xmlObject: XmlObject in result) {
                        if (xmlObject.isError()) {
                            manageError(xmlObject = xmlObject)
                            return
                        }
                    }
                subsonicApiRequester.subsonicState = SubsonicState.DATA_RECEIVED
                SubsonicState.DATA_RECEIVED.dataReceived = result
            } catch (_: IOException) {
                SubsonicState.ERROR.error = null
                subsonicApiRequester.subsonicState = SubsonicState.ERROR
            } catch (_: NullPointerException) {
                //Do nothing it's when lines has all been read
            } finally {
                input.close()
            }
        } catch (_: NullPointerException) {
            _logger?.warning("No body from request.")
            SubsonicState.ERROR.error = SubsonicErrorCode.DATA_NOT_FOUND
            subsonicApiRequester.subsonicState = SubsonicState.ERROR
        }
    }

    private fun manageError(xmlObject: XmlObject) {
        xmlObject as Error
        SubsonicState.ERROR.error =
            SubsonicErrorCode.getError(code = xmlObject.errorCode)
        subsonicApiRequester.subsonicState = SubsonicState.ERROR
    }

    protected fun setUnknownError() {
        _logger?.warning("Unknown error while fetching ping data.")
        subsonicApiRequester.subsonicState = SubsonicState.ERROR
        SubsonicState.ERROR.error = SubsonicErrorCode.UNKNOWN
    }

    /**
     * Checks if data has been received, if false then it set unknown error. otherwise do nothing
     */
    protected fun checkIfReceivedData() {
        if (subsonicApiRequester.subsonicState != SubsonicState.DATA_RECEIVED) setUnknownError()
    }
}
