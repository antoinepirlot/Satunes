///*
// * This file is part of Satunes.
// *
// * Satunes is free software: you can redistribute it and/or modify it under
// * the terms of the GNU General Public License as published by the Free Software Foundation,
// * either version 3 of the License, or (at your option) any later version.
// *
// * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
// * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// *
// * See the GNU General Public License for more details.
// *  You should have received a copy of the GNU General Public License along with Satunes.
// *
// * If not, see <https://www.gnu.org/licenses/>.
// *
// * **** INFORMATION ABOUT THE AUTHOR *****
// * The author of this file is Antoine Pirlot, the owner of this project.
// * You find this original project on Codeberg.
// *
// * My Codeberg link is: https://codeberg.org/antoinepirlot
// * This current project's link is: https://codeberg.org/antoinepirlot/Satunes
// */
//
//package io.github.antoinepirlot.satunes.internet.subsonic.utils
//
//import android.os.Build
//import android.util.Xml
//import androidx.annotation.RequiresApi
//import io.github.antoinepirlot.satunes.database.models.Album
//import io.github.antoinepirlot.satunes.database.models.Artist
//import io.github.antoinepirlot.satunes.database.models.Folder
//import io.github.antoinepirlot.satunes.database.models.Genre
//import io.github.antoinepirlot.satunes.database.models.Music
//import io.github.antoinepirlot.satunes.internet.subsonic.SubsonicApiRequester
//import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Error
//import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.Header
//import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlObject
//import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlSong
//import org.xmlpull.v1.XmlPullParser
//import org.xmlpull.v1.XmlPullParserException
//import java.io.IOException
//import java.io.InputStream
//import java.time.OffsetDateTime
//import androidx.core.net.toUri
//import io.github.antoinepirlot.satunes.internet.subsonic.models.media.SubsonicArtist
//import io.github.antoinepirlot.satunes.internet.subsonic.models.media.SubsonicFolder
//import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlAlbum
//import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlArtist
//import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlGenre
//import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.XmlMedia
//import io.github.antoinepirlot.satunes.internet.subsonic.models.responses.MusicFolder
//
///**
// * @author Antoine Pirlot 04/09/2025
// *
// * Inspired from https://developer.android.com/develop/connectivity/network-ops/xml
// */
//@RequiresApi(Build.VERSION_CODES.M)
//class SubsonicParser(private val subsonicApiRequester: SubsonicApiRequester) {
//    companion object {
//        private const val SONG_TAG_NAME = "song"
//        private const val ARTIST_TAG_NAME = "artists"
//        private const val ALBUMS_TAG_NAME = "albumArtists"
//        private const val GENRE_TAG_NAME = "genres"
//        private const val CONTRIBUTORS_TAG_NAME = "contributors"
//        private const val CONTRIBUTORS_ARTIST_TAG_NAME = "artist"
//        private const val MUSIC_FOLDERS_TAG_NAME = "musicFolders"
//        private const val SINGLE_MUSIC_FOLDER_TAG_NAME = "musicFolder"
//        private const val SINGLE_ALBUM_TAG_NAME = "album"
//    }
//
//    private lateinit var parser: XmlPullParser
//
//    @Throws(XmlPullParserException::class, IOException::class)
//    fun parse(inputStream: InputStream): List<XmlObject> {
//        inputStream.use { inputStream ->
//            parser = Xml.newPullParser()
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
//            parser.setInput(inputStream, null)
//            parser.nextTag()
//            return readAll()
//        }
//    }
//
//    private fun readAll(): List<XmlObject> {
//        val entries: MutableList<XmlObject> = mutableListOf()
//        entries.add(readHeader())
//        entries.addAll(readBody())
//        return entries
//    }
//
//    private fun readHeader(): Header {
//        requireStartTag(name = "subsonic-response")
//        return Header(
//            status = parser.getAttributeValue(null, "status"),
//            version = parser.getAttributeValue(null, "version"),
//            type = parser.getAttributeValue(null, "type"),
//            serverVersion = parser.getAttributeValue(null, "serverVersion"),
//            openSubsonic = parser.getAttributeValue(null, "openSubsonic").toBoolean()
//        )
//    }
//
//    private fun readBody(): List<XmlObject> {
//        val entries: MutableList<XmlObject> = mutableListOf()
//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.eventType == XmlPullParser.START_TAG) {
//                // Starts by looking for the entry tag.
//                when (parser.name) {
//                    "error" -> entries.add(readError())
//                    "randomSongs" -> entries.addAll(readRandomSongs())
//                    GENRE_TAG_NAME -> entries.add(element = readGenre())
//                    MUSIC_FOLDERS_TAG_NAME -> entries.addAll(elements = readMusicFolders())
//                    SINGLE_MUSIC_FOLDER_TAG_NAME -> entries.add(element = readMusicFolder())
//                    SINGLE_ALBUM_TAG_NAME -> entries.add(element = readAlbum())
//                    else -> skip()
//                }
//            }
//        }
//        return entries
//    }
//
//    private fun readError(): Error {
//        requireStartTag(name = "error")
//        val errorCode: Int = parser.getAttributeValue(null, "code").toInt()
//        val message: String = parser.getAttributeValue(null, "message")
//        return Error(code = errorCode, message = message)
//    }
//
//    private fun skip() {
//        if (parser.eventType != XmlPullParser.START_TAG) {
//            throw IllegalStateException()
//        }
//        var depth = 1
//        while (depth > 0) {
//            when (parser.nextTag()) {
//                XmlPullParser.END_TAG -> depth--
//                XmlPullParser.START_TAG -> depth++
//            }
//        }
//    }
//
//    private fun readRandomSongs(): MutableList<XmlSong> {
//        requireStartTag(name = "randomSongs")
//        parser.nextTag()
//        return readSongs()
//    }
//
//    private fun readSongs(): MutableList<XmlSong> {
//        val entries: MutableList<XmlSong> = mutableListOf()
//        while (parser.name == "song") {
//            parser.require(XmlPullParser.START_TAG, null, "song")
//            entries.add(element = readSong())
//        }
//        return entries
//    }
//
//    private fun readSong(): XmlSong {
//        requireStartTag(name = SONG_TAG_NAME)
//        val addedDateMs: Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            OffsetDateTime.parse(parser.getAttributeValue(null, "created"))
//                ?.toInstant()?.toEpochMilli()?: -1
//        } else -1
//
//        val subsonicId: String = parser.getAttributeValue(null, "id")
//        val musicUrl: String = subsonicApiRequester.getCommandUrl(
//            command = "getSong",
//            parameters = arrayOf("id=$subsonicId")
//        )
//        val title: String = parser.getAttributeValue(null, "title")
//        val duration: Long = parser.getAttributeValue(null, "duration").toLong()
//        val size: Int = parser.getAttributeValue(null, "size").toInt()
//        val albumId: String = parser.getAttributeValue(null, "albumId")
////        //Fetch album information
////        subsonicApiRequester.getAlbum(albumId = albumId)
//
//        var artist: Artist? = null
//        var genre: Genre? = null
//        parser.nextTag()
//        while (parser.name != SONG_TAG_NAME) {
//            //If it is song tag name, it means it's the end.
//            for(body in readBody()) {
//                if(!body.isMedia()) throw IllegalStateException("Building Music with non media tag.")
//                body as XmlMedia
//                if(body.isSong()) throw IllegalStateException("Can't have music inside music.")
//                if(body.isArtist()) artist = body.media as Artist
//                else if(body.isGenre()) genre = body.media as Genre
//                else throw IllegalStateException("No valid media.")
//            }
//        }
//        requireEndTag(name = SONG_TAG_NAME)
//
//        return XmlSong(music = Music(
//                id = -1,
//                subsonicId = subsonicId,
//                title = title,
//                displayName = "If you see this, it's a bug.",
//                absolutePath = "",
//                duration = duration,
//                size = size,
//                cdTrackNumber = -1, //TODO
//                addedDateMs = addedDateMs,
//                folder = Folder("Oh no"), //TODO
//                artist = artist!!,
//                album = Album("Title", artist = artist),//TODO
//                genre = genre!!, //TODO
//                uri = musicUrl.toUri()
//            )
//        )
//    }
//
//    private fun readGenre(): XmlGenre {
//        requireStartTag(name = GENRE_TAG_NAME)
//        return XmlGenre(Genre(parser.getAttributeValue(null, "name")))
//    }
//
//    private fun readArtist(): XmlArtist {
//        requireStartTag(name = ARTIST_TAG_NAME)
//        return XmlArtist(
//            SubsonicArtist(
//                subsonicId = parser.getAttributeValue(null, "id"),
//                title = parser.getAttributeValue(null, "name")
//            )
//        )
//    }
//
//    private fun readContributors(): List<XmlObject> {
//        requireStartTag(name = CONTRIBUTORS_TAG_NAME)
//        return readBody()
//    }
//
//    private fun readMusicFolders(): List<XmlObject> {
//        requireStartTag(name = MUSIC_FOLDERS_TAG_NAME)
//        return readBody()
//    }
//
//    private fun readMusicFolder(): MusicFolder {
//        requireStartTag(name = SINGLE_MUSIC_FOLDER_TAG_NAME)
//        val id: String = this.getAttribute(name = "id")
//        val title: String = this.getAttribute("name")
//        val folder = SubsonicFolder(subsonicId = id, title = title)
//        return MusicFolder(folder = folder)
//    }
//
//    private fun readAlbum(): XmlAlbum {
//        requireStartTag(name = SINGLE_ALBUM_TAG_NAME)
//        val id: String = this.getAttribute(name = "id")
//        val title: String = this.getAttribute(name = "name")
//
//        return XmlAlbum(subsonicId = id, title = title)
//    }
//
//    private fun requireStartTag(name: String) {
//        parser.require(XmlPullParser.START_TAG, null, name)
//    }
//
//    private fun requireEndTag(name: String) {
//        parser.require(XmlPullParser.START_TAG, null, name)
//    }
//
//    private fun getAttribute(name: String): String {
//        return parser.getAttributeValue(null, name)
//    }
//}