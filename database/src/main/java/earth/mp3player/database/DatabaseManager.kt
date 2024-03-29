/*
 *  This file is part of MP3 Player.
 *
 *  MP3 Player is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  MP3 Player is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MP3 Player.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  ***** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/MP3-Player
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package earth.mp3player.database

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import earth.mp3player.database.daos.GenreDAO
import earth.mp3player.database.daos.MusicDAO
import earth.mp3player.database.models.tables.Genre
import earth.mp3player.database.models.tables.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 27/03/2024
 */
class DatabaseManager(context: Context) {
    private val database: MP3PlayerDatabase = MP3PlayerDatabase.getDatabase(context = context)
    private val musicDao: MusicDAO = database.musicDao()
    private val genreDao: GenreDAO = database.genreDao()

    fun insertAll(vararg musics: Music) {
        CoroutineScope(Dispatchers.IO).launch {
            musicDao.insertAll(*musics)
        }
    }

    fun insertAll(vararg genres: Genre) {
        CoroutineScope(Dispatchers.IO).launch {
            genreDao.insertAll(*genres)
        }
    }

    fun getMusic(id: Long): MutableState<Music?> {
        val music: MutableState<Music?> = mutableStateOf(null)
        CoroutineScope(Dispatchers.IO).launch {
            music.value = musicDao.get(id = id)
        }
        return music
    }

    fun getGenre(id: Long): MutableState<Genre?> {
        val genre: MutableState<Genre?> = mutableStateOf(null)
        CoroutineScope(Dispatchers.IO).launch {
            genre.value = genreDao.getGenre(id = id)
        }
        return genre
    }
}