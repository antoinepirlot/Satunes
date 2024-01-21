package earth.mp3.ui.components.cards.tracks

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Music

@Composable
fun CardMusicList(
    modifier: Modifier = Modifier,
    musicList: List<Music>
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(musicList) { music: Music ->
            CardMusic(
                modifier = modifier,
                music = music,
            )
        }
    }
}

@Composable
@Preview
fun CardTrackListPreview() {
    CardMusicList(musicList = listOf())
}