package earth.mp3.ui.components.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.data.Music


@Composable
fun MusicCard(
    modifier: Modifier,
    music: Music
) {
    Column(
        modifier = modifier
    ) {
        Box(modifier = modifier) {
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