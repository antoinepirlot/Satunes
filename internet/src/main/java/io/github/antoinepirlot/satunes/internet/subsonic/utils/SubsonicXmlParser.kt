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

package io.github.antoinepirlot.satunes.internet.subsonic.utils

import android.util.Xml
import io.github.antoinepirlot.satunes.internet.subsonic.models.Error
import io.github.antoinepirlot.satunes.internet.subsonic.models.XmlObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

/**
 * @author Antoine Pirlot 04/09/2025
 *
 * Inspired from https://developer.android.com/develop/connectivity/network-ops/xml
 */
class SubsonicXmlParser {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<XmlObject> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readBody(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readBody(parser: XmlPullParser): List<XmlObject> {
        parser.require(XmlPullParser.START_TAG, null, "subsonic-response")
        val entries = mutableListOf<XmlObject>(
            XmlObject(
                status = parser.getAttributeValue(null, "status"),
                version = parser.getAttributeValue(null, "version"),
                type = parser.getAttributeValue(null, "type"),
                serverVersion = parser.getAttributeValue(null, "serverVersion"),
                openSubsonic = parser.getAttributeValue(null, "openSubsonic").toBoolean()
            )
        )
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag.
            if (parser.name == "error") {
                entries.add(readError(parser))
            } else {
                skip(parser)
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readError(parser: XmlPullParser): Error {
        parser.require(XmlPullParser.START_TAG, null, "error")
        val errorCode: Int = parser.getAttributeValue(null, "code").toInt()
        val message: String = parser.getAttributeValue(null, "message")
        return Error(errorCode = errorCode, message = message)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}