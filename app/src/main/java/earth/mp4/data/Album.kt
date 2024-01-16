package earth.mp4.data

data class Album(
    val id:Long,
    var songs: List<Song>,
    var artist: Artist?
)
