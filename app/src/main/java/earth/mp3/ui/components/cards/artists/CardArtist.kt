package earth.mp3.ui.components.cards.artists

import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CardArtist(
    modifier: Modifier = Modifier,
    artistName: String
) {
    TextButton(
        modifier = modifier,
        onClick = { /*TODO*/ }
    ) {
        Text(text = artistName)
    }
    Divider(modifier = modifier)
}

@Composable
@Preview
fun CardArtistPreview() {
    CardArtist(artistName = "Kylie Monologue")
}
