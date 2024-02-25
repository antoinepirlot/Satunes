package earth.mp3player.ui.utils

import java.util.concurrent.TimeUnit

fun getMillisToTimeText(milliseconds: Long): String {
    var toReturn = ""
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    if (hours > 0) {
        toReturn += "$hours:"
    }
    var minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
    minutes -= hours * 60
    if (minutes < 10) {
        toReturn += "0"
    }
    toReturn += "$minutes:"
    var seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
    seconds -= minutes * 60
    if (seconds < 10) {
        toReturn += "0"
    }
    return toReturn + "$seconds"
}