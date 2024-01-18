package earth.mp3.data

data class Album(
    val id:Long,
    var songs: List<Song>,
    var artist: Artist?
)
