package earth.mp4.data

data class Artist(
    val id: Long,
    var name: String,
    var albums: List<Album>
)
