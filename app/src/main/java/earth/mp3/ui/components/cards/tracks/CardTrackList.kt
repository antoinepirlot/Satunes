package earth.mp3.ui.components.cards.tracks

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CardTrackList(
    modifier: Modifier = Modifier
) {
    val tracksList = listOf(
        "Name of track 1",
        "Ilana Un monde imparfait"
    )
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(tracksList) { _: Int, trackName: String ->
            CardTrack(
                modifier = modifier,
                trackName = trackName,
            )
        }
    }
}

@Composable
@Preview
fun CardTrackListPreview() {
    CardTrackList()
}