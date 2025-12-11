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

package io.github.antoinepirlot.satunes.internet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import io.github.antoinepirlot.satunes.internet.subsonic.models.callbacks.SubsonicCallback
import okhttp3.Call
import java.util.Stack

/**
 * @author Antoine Pirlot 28/09/2025
 */
class SubsonicCall private constructor(
    private val _call: Call,
    internal val callBack: SubsonicCallback
) {
    companion object {
        private const val MAX_REQUESTS: Int = 5
        private val nextCalls: Stack<SubsonicCall> = Stack()
        private val currentCalls: MutableMap<SubsonicCallback, SubsonicCall> = mutableMapOf()

        var nbRequests: Int by mutableIntStateOf(0)
        private var size: Int = 0

        @Synchronized
        private fun pop(): SubsonicCall? {
            if (nextCalls.isEmpty()) return null
            this.nbRequests--
            return nextCalls.pop()
        }

        @Synchronized
        internal fun push(call: SubsonicCall) {
            this.nextCalls.push(call)
            this.nbRequests++
            this.executeNext()
        }

        @Synchronized
        private fun executeNext() {
            if (this.currentCalls.size >= MAX_REQUESTS) return
            val currentCall: SubsonicCall? = this.pop()
            if (currentCall != null) {
                this.currentCalls.put(key = currentCall.callBack, value = currentCall)
                this.size++
                currentCall.execute()
            }
        }

        @Synchronized
        fun getSize(): Int {
            return this.size
        }

        @Synchronized
        internal fun executionFinished(subsonicCallback: SubsonicCallback) {
            if (this.getSize() <= 0)
                throw IllegalStateException("No call to finish.")
            this.currentCalls.remove(key = subsonicCallback)
            this.size--
            this.executeNext()
        }
    }

    private var enqueued: Boolean = false

    internal fun enqueue() {
        if (this.enqueued) throw IllegalStateException("Already enqueued.")
        this.enqueued = true
        push(call = this)
    }


    private fun execute() {
        this._call.enqueue(callBack)
    }

    internal class Builder() {
        private lateinit var _call: Call
        private lateinit var _callBack: SubsonicCallback

        fun setCall(call: Call): Builder {
            if (this::_call.isInitialized) throw IllegalStateException("Call already set.")
            this._call = call
            return this
        }

        fun setCallBack(callBack: SubsonicCallback): Builder {
            if (this::_callBack.isInitialized)
                throw IllegalStateException("Callback already set.")
            this._callBack = callBack
            return this
        }

        fun build(): SubsonicCall {
            if (!this::_call.isInitialized) throw IllegalStateException("Call not set.")
            if (!this::_callBack.isInitialized) throw IllegalStateException("Callback not set.")

            return SubsonicCall(_call = this._call, callBack = this._callBack)
        }
    }
}