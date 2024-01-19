package earth.mp3.ui.components.cards.tracks

import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CardTrack(
    modifier: Modifier = Modifier,
    trackName: String,
) {
    TextButton(
        modifier = modifier,
        onClick = { /*TODO*/ } //redirect to the folder's view
    ) {
        Text(text = trackName)
    }
    Divider(modifier = modifier)
}

@Composable
@Preview
fun CardTrackPreview() {
    CardTrack(trackName = "trackName")
}