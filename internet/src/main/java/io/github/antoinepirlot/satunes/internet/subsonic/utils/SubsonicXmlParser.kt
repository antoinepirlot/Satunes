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

import android.os.Build
import android.util.Xml
import androidx.annotation.RequiresApi
import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Header
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlObject
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlSong
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.time.OffsetDateTime
import androidx.core.net.toUri
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlAlbum
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlArtist
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlGenre
import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlMedia

/**
 * @author Antoine Pirlot 04/09/2025
 *
 * Inspired from https://developer.android.com/develop/connectivity/network-ops/xml
 */
@RequiresApi(Build.VERSION_CODES.M)
class SubsonicXmlParser(private val subsonicApiRequester: SubsonicApiRequester) {
    companion object {
        private const val SONG_TAG_NAME = "song"
        private const val ARTIST_TAG_NAME = "artists"
        private const val ALBUMS_TAG_NAME = "albumArtists"
        private const val GENRE_TAG_NAME = "genres"
        private const val CONTRIBUTORS_TAG_NAME = "contributors"
        private const val CONTRIBUTORS_ARTIST_TAG_NAME = "artist"
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<XmlObject> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readAll(parser)
        }
    }

    private fun readAll(parser: XmlPullParser): List<XmlObject> {
        val entries: MutableList<XmlObject> = mutableListOf()
        entries.add(readHeader(parser = parser))
        entries.addAll(readBody(parser = parser))
        return entries
    }

    private fun readHeader(parser: XmlPullParser): Header {
        parser.require(XmlPullParser.START_TAG, null, "subsonic-response")
        return Header(
            status = parser.getAttributeValue(null, "status"),
            version = parser.getAttributeValue(null, "version"),
            type = parser.getAttributeValue(null, "type"),
            serverVersion = parser.getAttributeValue(null, "serverVersion"),
            openSubsonic = parser.getAttributeValue(null, "openSubsonic").toBoolean()
        )
    }

    private fun readBody(parser: XmlPullParser): List<XmlObject> {
        val entries: MutableList<XmlObject> = mutableListOf()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                // Starts by looking for the entry tag.
                when (parser.name) {
                    "error" -> entries.add(readError(parser = parser))
                    "randomSongs" -> entries.addAll(readRandomSongs(parser = parser))
                    GENRE_TAG_NAME -> entries.add(element = readGenre(parser = parser))
                    else -> skip(parser = parser)
                }
            }
        }
        return entries
    }

    private fun readError(parser: XmlPullParser): Error {
        parser.require(XmlPullParser.START_TAG, null, "error")
        val errorCode: Int = parser.getAttributeValue(null, "code").toInt()
        val message: String = parser.getAttributeValue(null, "message")
        return Error(errorCode = errorCode, message = message)
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth > 0) {
            when (parser.nextTag()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    private fun readRandomSongs(parser: XmlPullParser): MutableList<XmlSong> {
        parser.require(XmlPullParser.START_TAG, null, "randomSongs")
        parser.nextTag()
        return readSongs(parser = parser)
    }

    private fun readSongs(parser: XmlPullParser): MutableList<XmlSong> {
        val entries: MutableList<XmlSong> = mutableListOf()
        while (parser.name == "song") {
            parser.require(XmlPullParser.START_TAG, null, "song")
            entries.add(element = readSong(parser = parser))
        }
        return entries
    }

    private fun readSong(parser: XmlPullParser): XmlSong {
        parser.require(XmlPullParser.START_TAG, null, SONG_TAG_NAME)
        val addedDateMs: Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            OffsetDateTime.parse(parser.getAttributeValue(null, "created"))
                ?.toInstant()?.toEpochMilli()?: -1
        } else -1

        val subsonicId: String = parser.getAttributeValue(null, "id")
        val musicUrl: String = subsonicApiRequester.getCommandUrl(
            command = "getSong",
            parameters = arrayOf("id=$subsonicId")
        )
        val title: String = parser.getAttributeValue(null, "title")
        val duration: Long = parser.getAttributeValue(null, "duration").toLong()
        val size: Int = parser.getAttributeValue(null, "size").toInt()
        var artist: Artist? = null
        var genre: Genre? = null
        parser.nextTag()
        while (parser.name != SONG_TAG_NAME) {
            //If it is song tag name, it means it's the end.
            for(body in readBody(parser = parser)) {
                if(!body.isMedia()) throw IllegalStateException("Building Music with non media tag.")
                body as XmlMedia
                if(body.isSong()) throw IllegalStateException("Can't have music inside music.")
                if(body.isArtist()) artist = body.media as Artist
                else if(body.isGenre()) genre = body.media as Genre
                else throw IllegalStateException("No valid media.")
            }
        }
        parser.require(XmlPullParser.END_TAG, null, SONG_TAG_NAME)

        return XmlSong(music = Music(
                id = -1,
                subsonicId = subsonicId,
                title = title,
                displayName = "If you see this, it's a bug.",
                absolutePath = "",
                duration = duration,
                size = size,
                cdTrackNumber = -1, //TODO
                addedDateMs = addedDateMs,
                folder = Folder("Oh no"), //TODO
                artist = artist!!,
                album = Album("Title", artist = artist),//TODO
                genre = genre!!, //TODO
                uri = musicUrl.toUri()
            )
        )
    }

    private fun readGenre(parser: XmlPullParser): XmlGenre {
        parser.require(XmlPullParser.START_TAG, null, GENRE_TAG_NAME)
        return XmlGenre(Genre(parser.getAttributeValue(null, "name")))
    }

    private fun readArtist(parser: XmlPullParser): XmlArtist {
        parser.require(XmlPullParser.START_TAG, null, ARTIST_TAG_NAME)
        return XmlArtist(
            Artist(
                subsonicId = parser.getAttributeValue(null, "id"),
                title = parser.getAttributeValue(null, "name")
            )
        )
    }

    private fun readContributors(parser: XmlPullParser): List<XmlObject> {
        parser.require(XmlPullParser.START_TAG, null, CONTRIBUTORS_TAG_NAME)
        return readBody(parser = parser)
    }
}