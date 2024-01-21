package earth.mp3.ui.components.cards.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.MusicData

@Composable
fun CardMusicList(
    modifier: Modifier,
    musicDataList: List<MusicData>
) {
    LazyRow(
        modifier = modifier
    ) {
        itemsIndexed(musicDataList) { index: Int, musicData: MusicData ->
            MusicCard(modifier = modifier, musicData = musicData)
        }
    }

}

@Composable
@Preview
fun CardMusicListPreview() {
    val musicData = MusicData(1, "Il avait les mots", 2, 2, null, "relative path")
    val musicDataList = listOf<MusicData>(musicData)
    CardMusicList(modifier = Modifier.fillMaxSize(), musicDataList = musicDataList)
}