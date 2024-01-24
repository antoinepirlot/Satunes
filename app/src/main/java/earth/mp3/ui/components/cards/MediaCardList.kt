package earth.mp3.ui.components.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Media

@Composable
fun MediaCardList(
    modifier: Modifier = Modifier,
    mediaList: List<Media>,
    openFolder: (folder: Folder) -> Unit
) {
    val lazyState = rememberLazyListState()
    if (mediaList.isNotEmpty()) { // It fixes issue while accessing last folder in chain
        Column {
            LazyColumn(
                modifier = modifier,
                state = lazyState
            ) {
                items(mediaList) { media: Media ->
                    // First pair is image vector and second one is content description (String)
                    val pair = getRightIconAnDescription(media)
                    MediaCard(
                        modifier = modifier,
                        text = media.name,
                        imageVector = pair.first,
                        contentDescription = pair.second,
                        onClick = {
                            if (media is Folder) {
                                openFolder(media)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun CardListPreview() {
    MediaCardList(
        mediaList = listOf(),
        openFolder = {}
    )
}

fun getRightIconAnDescription(media: Media): Pair<ImageVector, String> {
    when (media) {
        is Folder -> {
            return Icons.Filled.ArrowForward to "Arrow Forward"
        }

        is Artist -> {
            return Icons.Filled.AccountCircle to "Account Circle"
        }
    }
    // In that case, media is Music
    return Icons.Filled.PlayArrow to "Play Arrow"
}