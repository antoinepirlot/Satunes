package earth.mp3.ui.components.music.bars

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ShowCurrentMusicButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    LargeFloatingActionButton(
        onClick = onClick
    ) {
        Icon(
            modifier = modifier.size(60.dp),
            imageVector = Icons.Rounded.Audiotrack,
            contentDescription = "Show current music icon"
        )
    }
}

@Composable
@Preview
fun ShowCurrentMusicPreview() {
    ShowCurrentMusicButton(onClick = {})
}