package earth.mp3.models

class Artist(
    val id: Long,
    override var name: String,
    val numberOfTracks: Int,
    val numberOfAlbums: Int,
    val musicList: MutableList<Music> = mutableListOf(),
    //val albumList: MutableList<Album> = mutableListOf()
) : Media {

    override fun toString(): String {
        return this.name
    }
}