package earth.mp3.ui.components.music

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.R

@Composable
fun PlayAllButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    //TODO change it to use floating action button
    Box(modifier = modifier) {
        TextButton(onClick = onClick) {
            ListItem(
                headlineContent = {
                    Text(text = stringResource(id = R.string.play_all))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = "Play All Musics Button"
                    )
                }
            )
        }
    }
//    Row(
//        modifier = modifier,
//        horizontalArrangement = Arrangement.Start
//        ) {
//        FloatingActionButton(
//            modifier = modifier,
//            onClick = { /*TODO*/ },
//        ) {
//            Icon(
//                modifier = modifier.size(40.dp),
//                imageVector = Icons.Filled.PlayArrow,
//                contentDescription = "Play all music"
//            )
//        }
//    }
}

@Composable
@Preview
fun PlayAllButtonPreview() {
    PlayAllButton(onClick = {})
}