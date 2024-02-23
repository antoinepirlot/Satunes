package earth.mp3.ui.components.cards.media

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Artist
import earth.mp3.models.Folder
import earth.mp3.models.Media
import java.util.SortedMap

@Composable
fun MediaCardList(
    modifier: Modifier = Modifier,
    mediaMap: SortedMap<Long, Media>,
    openMedia: (media: Media) -> Unit
) {
    val lazyState = rememberLazyListState()
    if (mediaMap.isNotEmpty()) { // It fixes issue while accessing last folder in chain
        Column {
            LazyColumn(
                modifier = modifier,
                state = lazyState
            ) {
                items(
                    items = mediaMap.values.toList(),
                    key = { it.id }
                ) { media: Media ->
                    // First pair is image vector and second one is content description (String)
                    val pair = getRightIconAnDescription(media)
                    MediaCard(
                        modifier = modifier,
                        text = media.name,
                        imageVector = pair.first,
                        contentDescription = pair.second,
                        onClick = { openMedia(media) }
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
        mediaMap = sortedMapOf(),
        openMedia = {}
    )
}

private fun getRightIconAnDescription(media: Media): Pair<ImageVector, String> {
    return when (media) {
        is Folder -> {
            Icons.Filled.Folder to "Arrow Forward"
        }

        is Artist -> {
            Icons.Filled.AccountCircle to "Account Circle"
        }

        else -> {
            // In that case, media is Music
            Icons.Filled.MusicNote to "Play Arrow"
        }
    }

}