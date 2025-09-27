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
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.SubsonicErrorCode
import io.github.antoinepirlot.satunes.internet.subsonic.models.SubsonicState
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.SubsonicResponse
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.SubsonicResponseBody
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.io.InputStream


/**
 * @author Antoine Pirlot 03/09/2025
 */

@RequiresApi(Build.VERSION_CODES.M)
internal abstract class SubsonicCallback(
    protected val subsonicApiRequester: SubsonicApiRequester,
    protected val onSucceed: (() -> Unit)? //Used in children classes
) : Callback {

    private val _logger: SatunesLogger? = SatunesLogger.getLogger()

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
        SubsonicState.ERROR.error = null
        subsonicApiRequester.subsonicState = SubsonicState.ERROR
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun onResponse(call: Call, response: Response) {
        if (response.code >= 400) {
            SubsonicState.ERROR.error = SubsonicErrorCode.GENERIC_ERROR
            subsonicApiRequester.subsonicState = SubsonicState.ERROR
            return
        }
        var hasError: Boolean = false
        try {
            val input: InputStream = response.body!!.byteStream()
            try {
                val response: SubsonicResponse =
                    Json.decodeFromStream<SubsonicResponseBody>(input).subsonicResponse

                //Maybe error
                if (response.isError()) {
                    manageError(error = response.error!!)
                } else {
                    subsonicApiRequester.subsonicState = SubsonicState.DATA_RECEIVED
                    SubsonicState.DATA_RECEIVED.addDataReceived(subsonicCallback = this, response)
                }
            } catch (e: SerializationException) {
                _logger?.severe(e.message)
                hasError = true
            } catch (e: IllegalArgumentException) {
                _logger?.severe(e.message)
                hasError = true
            } catch (e: IOException) {
                _logger?.severe(e.message)
                SubsonicState.ERROR.error = null
                subsonicApiRequester.subsonicState = SubsonicState.ERROR
            } catch (_: NullPointerException) {
                //Do nothing it's when lines has all been read
            } finally {
                input.close()
            }
        } catch (_: NullPointerException) {
            _logger?.warning("No body from request.")
            hasError = true
        } finally {
            if (hasError) {
                SubsonicState.ERROR.error = SubsonicErrorCode.DATA_NOT_FOUND
                subsonicApiRequester.subsonicState = SubsonicState.ERROR
            }
        }
    }

    private fun manageError(error: Error) {
        SubsonicState.ERROR.error = SubsonicErrorCode.getError(code = error.code)
        subsonicApiRequester.subsonicState = SubsonicState.ERROR
    }

    protected fun setUnknownError() {
        _logger?.warning("Unknown error while fetching ping data.")
        subsonicApiRequester.subsonicState = SubsonicState.ERROR
        SubsonicState.ERROR.error = SubsonicErrorCode.UNKNOWN
    }

    /**
     * Checks if data has been received, if false then it set unknown error and returns false. otherwise return true
     */
    protected fun hasReceivedData(): Boolean {
        return if (subsonicApiRequester.subsonicState != SubsonicState.DATA_RECEIVED) {
            setUnknownError()
            false
        } else true
    }

    /**
     * Change state to idle for [SubsonicState]
     */
    protected fun dataProcessed() {
        if(!SubsonicState.DATA_RECEIVED.hasDataToProcess())
            subsonicApiRequester.subsonicState = SubsonicState.IDLE
    }

    protected fun getSubsonicResponse(): SubsonicResponse {
        return SubsonicState.DATA_RECEIVED.getDataReceived(subsonicCallback = this)!!
    }
}
