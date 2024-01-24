package earth.mp3.ui.components.cards

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
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
    parentMedia: Media? = null, // For example folder, it contains a list of music
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: (media: Media) -> Unit
) {
    val lazyState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyState
    ) {
        items(mediaList) { media: Media ->
            MediaCard(
                modifier = modifier,
                text = media.name,
                imageVector = imageVector,
                contentDescription = contentDescription,
                onClick = { onClick(media) }
            )
        }
    }
    if (parentMedia is Folder) {
        // TODO find a parade to avoid abstract class as Android Studio tell me when the condition
        //  is parentMedia is Folder || parentMedia is Artist
        // Show music list
        MediaCardList(
            mediaList = parentMedia.musicList,
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "Play Arrow",
            onClick = { /* TODO */ }
        )
    }
    if (parentMedia is Artist) {
        // TODO find a parade to avoid abstract class as Android Studio tell me when the condition
        //  is parentMedia is Folder || parentMedia is Artist
        // Show music list
        MediaCardList(
            mediaList = parentMedia.musicList,
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "Play Arrow",
            onClick = { /* TODO */ }
        )
    }
}

@Composable
@Preview
fun CardListPreview() {
    MediaCardList(
        mediaList = listOf<Media>(),
        imageVector = Icons.Filled.PlayArrow,
        onClick = {}
    )
}