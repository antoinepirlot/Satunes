package earth.mp3.ui.utils

import androidx.media3.common.MediaItem
import earth.mp3.models.Folder
import earth.mp3.models.Media
import earth.mp3.models.Music
import earth.mp3.services.PlaybackController
import java.util.SortedMap


/**
 * Start the music
 *
 * @param mediaToPlay the music to play from the music list
 */

fun startMusic(mediaToPlay: Media? = null) {
    val playbackController = PlaybackController.getInstance()

    when (mediaToPlay) {
        is Music -> {
            playbackController.start(mediaToPlay)
        }

        is Folder -> {
            playbackController.start()
        }

        null -> {
            playbackController.start()
        }
    }
}

/**
 * Create the list of all music in the folder and subfolder
 */
fun getMusicListFromFolder(folder: Folder): SortedMap<Music, MediaItem> {
    val mapOfMusic: SortedMap<Music, MediaItem> = folder.musicMediaItemSortedMap
    for (subfolder in folder.getSubFolderList().values) {
        mapOfMusic.putAll(subfolder.musicMediaItemSortedMap)
    }
    return mapOfMusic
}