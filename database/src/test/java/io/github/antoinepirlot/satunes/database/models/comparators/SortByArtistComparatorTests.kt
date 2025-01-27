/*
 * This file is part of Satunes.
 *
 * Satunes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Satunes.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 * *** INFORMATION ABOUT THE AUTHOR *****
 * The author of this file is Antoine Pirlot, the owner of this project.
 * You find this original project on github.
 *
 * My github link is: https://github.com/antoinepirlot
 * This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 * PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.models.comparators

import io.github.antoinepirlot.satunes.database.models.Album
import io.github.antoinepirlot.satunes.database.models.Artist
import io.github.antoinepirlot.satunes.database.models.Folder
import io.github.antoinepirlot.satunes.database.models.Genre
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.satunes.utils.logger.SatunesLogger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SortByArtistComparatorTests {
    private val _musics: MutableCollection<Music> = mutableSetOf()

    init {
        SatunesLogger.enabled = false
        for (i: Long in 0L..1000L) {

            val artist = Artist(getRandomWord())
            val music = Music(
                i,
                getRandomWord(),
                getRandomWord(),
                getRandomPath(),
                (1L..9000L).random(),
                (1..9000).random(),
                null,
                Folder(getRandomWord()),
                artist,
                Album(getRandomWord(), artist),
                Genre(getRandomWord()),
                uri = null
            )
            _musics.add(music)
        }
    }

    private fun getRandomWord(): String {
        var word = ""
        val wordLength: Int = (1..25).random()
        for (i: Int in 0..wordLength) word += (0..255).random().toChar()
        return word
    }

    private fun getRandomPath(): String {
        var path = ""
        val nbFolders: Int = (1..6).random()
        for (i: Int in 0..nbFolders) path += '/' + getRandomWord()
        path += '/' + getRandomWord() + ".mp3"
        return path
    }

    @Test
    fun sortTest() {
        val sortedList: List<Music> = _musics.sortedWith(SortByArtistComparator)

        var lastMusic: Music = sortedList[0]
        for (i: Int in 1..<sortedList.size) {
            val currentMusic: Music = sortedList[i]
            val cmp: Int = SortByArtistComparator.compare(currentMusic, lastMusic)
            println("cmp is: $cmp")
            if (currentMusic.artist == lastMusic.artist) {
                val titleCmp: Int = StringComparator.compare(currentMusic.title, lastMusic.title)
                if (titleCmp == 0) assertEquals(0, cmp)
                else {
                    println("titleCmp is: $titleCmp")
                    if (titleCmp > 0) assertTrue(cmp > 0)
                    else assertTrue(cmp < 0)
                }
            } else {
                val artistTitleCmp: Int =
                    StringComparator.compare(currentMusic.artist.title, lastMusic.artist.title)
                println("artistTitleCmp is: $artistTitleCmp")
                if (artistTitleCmp == 0) assertEquals(0, cmp)
                else if (artistTitleCmp > 0) assertTrue(cmp > 0)
                else assertTrue(cmp < 0)
            }
            lastMusic = currentMusic
        }
    }
}