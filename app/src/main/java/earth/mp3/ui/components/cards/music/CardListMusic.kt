package earth.mp3.ui.components.cards.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Music

@Composable
fun CardMusicList(
    modifier: Modifier,
    musicDataList: List<Music>
) {
    val lazyState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyState
    ) {
        itemsIndexed(musicDataList) { index: Int, music: Music ->
            MusicCard(modifier = modifier, music = music, onClick = { /*TODO play music*/ })
        }
    }

}

@Composable
@Preview
fun CardMusicListPreview() {
    val musicData = Music(1, "Il avait les mots", 2, 2, null, "relative path")
    val musicDataList = listOf<Music>(musicData)
    CardMusicList(modifier = Modifier.fillMaxSize(), musicDataList = musicDataList)
}