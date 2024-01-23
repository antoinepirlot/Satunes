package earth.mp3.models

class Artist(id: Long, name: String, numberOfTracks: Int, numberOfAlbums: Int) {
    val id: Long = id
    var name: String = name
    var numberOfTracks: Int = numberOfTracks
    var numberOfAlbums: Int = numberOfAlbums
    val musicList: MutableList<Music> = mutableListOf()
    //val albumList: MutableList<Album> = mutableListOf()

    override fun toString(): String {
        return this.name
    }
}