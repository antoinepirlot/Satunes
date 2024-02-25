package earth.mp3player.models

interface Media : Comparable<Music> {
    val id: Long
    val name: String

    override fun compareTo(other: Music): Int {
        return if (other.id == this.id) {
            0
        } else if (other.id < this.id) {
            1
        } else {
            -1
        }
    }
}