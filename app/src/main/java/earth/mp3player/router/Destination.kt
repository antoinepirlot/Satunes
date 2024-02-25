package earth.mp3player.router

enum class Destination(val link: String) {
    FOLDERS("/folders"),
    ARTISTS("/artists"),
    MUSICS("/musics"),
    PLAYBACK("/playback")
}