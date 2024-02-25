package earth.mp3player.ui.components.music.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3player.R

@Composable
fun ShuffleAllButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        TextButton(onClick = onClick) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.play_all))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = "Shuffle All Musics Button"
                    )
                }
            )
        }
    }
}

@Composable
@Preview
fun PlayAllButtonPreview() {
    ShuffleAllButton(onClick = {})
}