/*
 * This file is part of Satunes.
 *
 *  Satunes is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software Foundation,
 *  either version 3 of the License, or (at your option) any later version.
 *
 *  Satunes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with Satunes.
 *  If not, see <https://www.gnu.org/licenses/>.
 *
 *  **** INFORMATIONS ABOUT THE AUTHOR *****
 *  The author of this file is Antoine Pirlot, the owner of this project.
 *  You find this original project on github.
 *
 *  My github link is: https://github.com/antoinepirlot
 *  This current project's link is: https://github.com/antoinepirlot/Satunes
 *
 *  You can contact me via my email: pirlot.antoine@outlook.com
 *  PS: I don't answer quickly.
 */

package io.github.antoinepirlot.satunes.database.services

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.github.antoinepirlot.satunes.database.R
import io.github.antoinepirlot.satunes.database.models.Music
import io.github.antoinepirlot.utils.showToastOnUiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Antoine Pirlot on 06/07/2024
 */
object DataUpdater {

    val isUpdating: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Update music information to files on storage then, update data stored in the app
     *
     * @param music to be updated
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun update(context: Context, music: Music) {
        isUpdating.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val values = ContentValues()

            try {
                val rowUpdatedCount: Int = context.contentResolver.update(
                    DataLoader.URI,
                    values,
                    "${MediaStore.Audio.Media._ID} = ${music.id}",
                    null
                )

                if (rowUpdatedCount > 0) {
                    DataManager.updateMusic(music = music)
                    DataManager.updateAlbum(album = music.album)
                    DataManager.updateGenre(genre = music.genre)
                    DataManager.updateArtist(artist = music.artist)
                    showToastOnUiThread(
                        context = context,
                        message = context.getString(R.string.update_success)
                    )
                } else {
                    showToastOnUiThread(
                        context = context,
                        message = context.getString(R.string.update_none)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToastOnUiThread(
                    context = context,
                    message = context.getString(R.string.update_failed)
                )
            } finally {
                isUpdating.value = false
            }
        }
    }

    private fun putValues(values: ContentValues, music: Music) {
        values.put(MediaStore.Audio.Media.TITLE, music.title)
        values.put(MediaStore.Audio.Albums.ALBUM, music.album.title)
        values.put(MediaStore.Audio.Artists.ARTIST, music.artist.title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            values.put(MediaStore.Audio.Media.GENRE, music.genre.title)
        }
    }
}