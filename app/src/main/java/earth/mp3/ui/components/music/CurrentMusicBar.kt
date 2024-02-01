package earth.mp3.ui.components.music

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CurrentMusicBar(
    modifier: Modifier = Modifier,

    ) {
    var visible by remember { mutableStateOf(true) }
    TextButton(onClick = { visible = !visible }) {
        AnimatedVisibility(visible = visible,
            enter = expandVertically(expandFrom = Alignment.Top) { 20 },
            exit = shrinkVertically(animationSpec = tween()) { fullHeight -> fullHeight / 2 }
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { visible = !visible }) {
                Image(imageVector = Icons.Default.MusicNote, contentDescription = "Musique")
            }
        }
    }
}


@Composable
@Preview
fun CurrentMusicBarPreview() {
    CurrentMusicBar()
}