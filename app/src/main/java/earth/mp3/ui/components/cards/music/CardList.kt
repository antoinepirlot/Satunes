package earth.mp3.ui.components.cards.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.data.Music

@Composable
fun CardList(
    modifier: Modifier,
    musicList: List<Music>
) {
    LazyRow(
        modifier = modifier
    ) {
        itemsIndexed(musicList) { index: Int, music: Music ->
            MusicCard(modifier = modifier, music = music)
        }
    }

}

@Composable
@Preview
fun CardListPreview() {
    val music = Music(1, "Il avait les mots", 2, 2, null)
    val musicList = listOf<Music>(music)
    CardList(modifier = Modifier.fillMaxSize(), musicList = musicList)
}