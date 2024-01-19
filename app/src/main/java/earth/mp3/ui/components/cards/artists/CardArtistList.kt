package earth.mp3.ui.components.cards.artists

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CardArtistList(
    modifier: Modifier = Modifier
) {
    val artistsList = listOf(
        "Kylie Monologue",
        "Dafpounk"
    )
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(artistsList) { _: Int, artistName: String ->
            CardArtist(
                modifier = modifier,
                artistName = artistName,
            )
        }
    }
}

@Composable
@Preview
fun CardArtistListPreview() {
    CardArtistList()
}