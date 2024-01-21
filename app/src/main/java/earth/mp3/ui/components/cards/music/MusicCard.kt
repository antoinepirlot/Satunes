package earth.mp3.ui.components.cards.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Music


@Composable
fun MusicCard(
    modifier: Modifier,
    music: Music,
    onClick: () -> Unit
) {
    //TODO make generic card
    Box(modifier = modifier) {
        TextButton(onClick = onClick) {
            ListItem(
                headlineContent = {
                    Text(text = music.name)
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play Arrow"
                    )
                }
            )
        }
    }
    Divider(modifier = modifier)
}

@Composable
@Preview
fun MusicCardPreview() {
    val musicData = Music(1, "Il avait les mots", 2, 2, null, "relative path")
    MusicCard(modifier = Modifier.fillMaxSize(), musicData, onClick = {})
}