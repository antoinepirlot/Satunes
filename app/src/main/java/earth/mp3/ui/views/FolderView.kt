package earth.mp3.ui.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Folder

@Composable
fun FolderView(
    modifier: Modifier = Modifier,
    folder: Folder
) {
    Text(text = folder.getName())
}

@Composable
@Preview
fun FolderViewPreview() {
    FolderView(folder = Folder(0, "Test"))
}