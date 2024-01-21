package earth.mp3.ui.components.cards.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3.models.MusicData


@Composable
fun MusicCard(
    modifier: Modifier,
    musicData: MusicData
) {
    Column(
        modifier = modifier
    ) {
        Box(modifier = modifier.padding(8.dp)) {
            Text(text = musicData.name)
        }
    }
}

@Composable
@Preview
fun MusicCardPreview() {
    val musicData = MusicData(1, "Il avait les mots", 2, 2, null, "relative path")
    MusicCard(modifier = Modifier.fillMaxSize(), musicData)
}