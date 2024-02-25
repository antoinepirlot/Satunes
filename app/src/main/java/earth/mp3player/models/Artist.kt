package earth.mp3player.models

class Artist(
    override val id: Long,
    override var name: String,
    val numberOfTracks: Int = 0,
    val numberOfAlbums: Int = 0,
    val musicList: MutableList<Music> = mutableListOf(),
    //val albumList: MutableList<Album> = mutableListOf()
) : Media {



    override fun toString(): String {
        return this.name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artist

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}