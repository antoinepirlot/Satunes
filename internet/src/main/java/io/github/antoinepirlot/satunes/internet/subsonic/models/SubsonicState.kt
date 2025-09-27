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
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.SubsonicCallback
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.SubsonicResponse

/**
 * @author Antoine Pirlot 03/09/2025
 */

@RequiresApi(Build.VERSION_CODES.M)
enum class SubsonicState {
    DISCONNECTED,
    PINGING,
    IDLE,
    REQUESTING,
    DATA_RECEIVED,
    ERROR;

    var error: SubsonicErrorCode? = null
        set(value) {
            if (value != null && this != ERROR)
                throw UnsupportedOperationException("Can't change code of non error state.")
            field = value
        }

    //dataReceived is only used for DATA_RECEIVED
    private val dataReceivedMap: MutableMap<SubsonicCallback, SubsonicResponse?> = mutableMapOf()
        get() {
            this.checkIsDataReceived()
            return field
        }

    internal fun prepareDataReceived(subsonicCallback: SubsonicCallback) {
        this.checkIsDataReceived()
        this.dataReceivedMap[subsonicCallback] = null
    }

    internal fun addDataReceived(subsonicCallback: SubsonicCallback, subsonicResponse: SubsonicResponse) {
        this.checkIsDataReceived()
        this.dataReceivedMap[subsonicCallback] = subsonicResponse
    }

    internal fun getDataReceived(subsonicCallback: SubsonicCallback): SubsonicResponse? {
        this.checkIsDataReceived()
        return this.dataReceivedMap.remove(key = subsonicCallback)
    }

    internal fun hasDataToProcess(): Boolean {
        this.checkIsDataReceived()
        return this.dataReceivedMap.isNotEmpty()
    }

    private fun checkIsDataReceived() {
        if(this != DATA_RECEIVED) throw UnsupportedOperationException("Not Allowed.")
    }
}