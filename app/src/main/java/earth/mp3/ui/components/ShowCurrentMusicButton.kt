package earth.mp3.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun ShowCurrentMusicButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = Icons.Rounded.Audiotrack, contentDescription = "Show current music icon")
    }
}

@Composable
@Preview
fun ShowCurrentMusicPreview() {
    ShowCurrentMusicButton(onClick = {})
}