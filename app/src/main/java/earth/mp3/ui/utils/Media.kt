package earth.mp3.ui.utils

import earth.mp3.models.ExoPlayerManager
import earth.mp3.models.Music

/**
 * Start the music from music list.
 *
 * @param musicList the music list of musics to play
 * @param musicToPlay the music to play from the music list
 */

fun startMusicFromPlaylist(musicList: List<Music>, musicToPlay: Music) {
    val exoPlayerManager = ExoPlayerManager.getInstance(null)
    exoPlayerManager.loadMusic(musicList)
    exoPlayerManager.start(musicToPlay)
}