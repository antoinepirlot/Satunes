package earth.mp3player.ui.views

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.models.Media
import earth.mp3player.models.Music
import earth.mp3player.services.PlaybackController
import earth.mp3player.ui.components.cards.media.MediaCardList
import earth.mp3player.ui.components.music.bars.ShowCurrentMusicButton
import earth.mp3player.ui.components.music.buttons.ShuffleAllButton
import java.util.SortedMap

@Composable
fun MediaListView(
    modifier: Modifier = Modifier,
    mediaMap: SortedMap<Long, Media>,
    openMedia: (media: Media) -> Unit,
    shuffleMusicAction: () -> Unit,
    onFABClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            if (PlaybackController.getInstance().musicPlaying.value != null) {
                ShowCurrentMusicButton(onClick = onFABClick)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ShuffleAllButton(onClick = shuffleMusicAction)
            MediaCardList(mediaMap = mediaMap, openMedia = openMedia)
        }
    }
}

@Composable
@Preview
fun MediaListViewPreview() {
    val map = sortedMapOf(Pair<Long, Media>(1, Music(1, "Musique", 0, 0, Uri.EMPTY, "", null)))
    MediaListView(
        mediaMap = map,
        openMedia = {},
        shuffleMusicAction = {},
        onFABClick = {}
    )
}