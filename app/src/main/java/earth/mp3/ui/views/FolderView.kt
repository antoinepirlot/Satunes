package earth.mp3.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.ui.components.cards.folder.CardFolderList

@Composable
fun FolderView(
    modifier: Modifier = Modifier,
) {
    CardFolderList(modifier = modifier)
}

@Composable
@Preview
fun FolderViewPreview() {
    FolderView()
}