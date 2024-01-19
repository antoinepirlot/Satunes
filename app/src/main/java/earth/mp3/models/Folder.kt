package earth.mp3.models

data class Folder(
    val relativePath: String,
    val subFolders: MutableMap<String, Folder> = mutableMapOf(),
    val musicList: MutableList<Music> = mutableListOf()
)