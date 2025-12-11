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

import io.github.antoinepirlot.android.utils.logger.Logger
import io.github.antoinepirlot.satunes.internet.SubsonicCall
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.SubsonicErrorCode
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.SubsonicResponse
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.SubsonicResponseBody
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

internal abstract class SubsonicCallback(
    protected val subsonicApiRequester: SubsonicApiRequester,
    protected val onSucceed: (() -> Unit)?, //Used in children classes
    protected val onError: ((Error) -> Unit)?,
) : Callback {

    private val _logger: Logger? = Logger.getLogger()
    protected var response: SubsonicResponse? = null

    override fun onFailure(call: Call, e: IOException) {
        _logger?.severe(e.message)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun onResponse(call: Call, response: Response) {
        if (response.code >= 400) {
            //TODO
            return
        }
        try {
            val input: InputStream = response.body.byteStream()
            try {
                val format = Json { ignoreUnknownKeys = true }
                val response: SubsonicResponse =
                    format.decodeFromStream<SubsonicResponseBody>(input).subsonicResponse

                if (response.isError()) this.onError?.invoke(response.error!!)
                this.response = response

            } catch (e: SerializationException) {
                _logger?.severe(e.message)
            } catch (e: IllegalArgumentException) {
                _logger?.severe(e.message)
            } catch (e: IOException) {
                _logger?.severe(e.message)
            } catch (_: NullPointerException) {
                //Do nothing it's when lines has all been read
            } finally {
                input.close()
            }
        } catch (_: NullPointerException) {
            _logger?.warning("No body from request.")
        } finally {
            SubsonicCall.executionFinished(subsonicCallback = this)
        }
    }
}
