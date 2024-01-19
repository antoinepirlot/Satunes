package earth.mp3.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.ui.components.cards.artists.CardArtistList

@Composable
fun ArtistsView(
    modifier: Modifier = Modifier
) {
    CardArtistList(modifier = modifier)
}

@Composable
@Preview
fun ArtistsViewPreview() {
    ArtistsView()
}