package earth.mp3.ui.components.cards

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Music

@Composable
fun MediaCard(
    modifier: Modifier = Modifier,
    text: String,
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        TextButton(onClick = onClick) {
            ListItem(
                headlineContent = {
                    Text(text = text)
                },
                leadingContent = {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = contentDescription
                    )
                }
            )
        }
    }
    Divider(modifier = modifier)
}

@Composable
@Preview
fun CardPreview() {
    val music = Music(1, "Il avait les mots", 2, 2, null, "relative path")
    MediaCard(
        modifier = Modifier.fillMaxSize(),
        text = music.name,
        imageVector = Icons.Filled.PlayArrow,
        onClick = {})
}