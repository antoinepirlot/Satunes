package earth.mp3.ui.utils

import earth.mp3.models.Folder
import earth.mp3.models.Media
import earth.mp3.models.Music
import earth.mp3.services.PlaybackController
import java.util.SortedMap


/**
 * Start the music from music list.
 *
 * If the shuffle mode is true, then start music in shuffle mode and start with mediaToPlay.
 *
 * @param musicMap the music map of musics to play
 * @param mediaToPlay the music to play from the music list
 * @param shuffleMode indicate if the music start in shuffle mode, by default it is false
 */

fun startMusic(musicMap: SortedMap<Long, Music>, mediaToPlay: Media, shuffleMode: Boolean = false) {
    val playbackController = PlaybackController.getInstance()
    playbackController.loadMusic(musicMap, shuffleMode = shuffleMode)

    when (mediaToPlay) {
        is Music -> {
            playbackController.start(mediaToPlay)
        }

        is Folder -> {
            playbackController.start()
        }
    }
}

/**
 * Create the list of all music in the folder and subfolder
 */
fun getMusicListFromFolder(folder: Folder): SortedMap<Long, Music> {
    val mapOfMusic: SortedMap<Long, Music> = folder.musicMap
    for (subfolder in folder.getSubFolderList().values) {
        mapOfMusic.putAll(subfolder.musicMap)
    }
    return mapOfMusic
}