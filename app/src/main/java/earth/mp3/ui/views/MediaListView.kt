package earth.mp3.ui.views

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Media
import earth.mp3.models.Music
import earth.mp3.ui.components.ShowCurrentMusicButton
import earth.mp3.ui.components.cards.media.MediaCardList
import earth.mp3.ui.components.music.PlayAllButton

@Composable
fun MediaListView(
    modifier: Modifier = Modifier,
    mediaList: List<Media>,
    openMedia: (media: Media) -> Unit,
    playMusicAction: () -> Unit,
    onFABClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = { ShowCurrentMusicButton(onClick = onFABClick) },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            PlayAllButton(onClick = playMusicAction)
            MediaCardList(mediaList = mediaList, openMedia = openMedia)
        }
    }
}

@Composable
@Preview
fun MediaListViewPreview() {
    MediaListView(
        mediaList = listOf(Music(1, "Musique", 0, 0, Uri.EMPTY, "", null)),
        openMedia = {},
        playMusicAction = {},
        onFABClick = {}
    )
}