package earth.mp3.ui.utils

import earth.mp3.models.ExoPlayerManager
import earth.mp3.models.Music

/**
 * Start playback and load music if no music in queue
 */
fun startPlaylist(musicList: List<Music>?) {
    val exoPlayerManager = ExoPlayerManager.getInstance(null)
    if (musicList != null) {
        exoPlayerManager.loadMusic(musicList)
    }
    exoPlayerManager.startMusic()
}