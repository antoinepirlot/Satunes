package earth.mp3.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.ui.components.cards.tracks.CardTrackList

@Composable
fun TracksView(
    modifier: Modifier = Modifier
) {
    CardTrackList(modifier = modifier)
}

@Composable
@Preview
fun TracksViewPreview() {
    TracksView()
}