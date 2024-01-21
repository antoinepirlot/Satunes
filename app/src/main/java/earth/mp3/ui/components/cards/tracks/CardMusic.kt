package earth.mp3.ui.components.cards.tracks

import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Music

@Composable
fun CardMusic(
    modifier: Modifier = Modifier,
    music: Music,
) {
    TextButton(
        modifier = modifier,
        onClick = { /*TODO*/ } //redirect to the folder's view
    ) {
        Text(text = music.name)
    }
    Divider(modifier = modifier)
}

@Composable
@Preview
fun CardTrackPreview() {
    CardMusic(music = Music(0, "Music Name", 2, 5, null, "/Music"))
}