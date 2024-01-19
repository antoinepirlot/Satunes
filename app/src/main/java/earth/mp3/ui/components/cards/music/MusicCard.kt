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
import earth.mp3.models.Music


@Composable
fun MusicCard(
    modifier: Modifier,
    music: Music
) {
    Column(
        modifier = modifier
    ) {
        Box(modifier = modifier.padding(8.dp)) {
            Text(text = music.name)
        }
    }
}

@Composable
@Preview
fun MusicCardPreview() {
    val music = Music(1, "Il avait les mots", 2, 2, null)
    MusicCard(modifier = Modifier.fillMaxSize(), music)
}