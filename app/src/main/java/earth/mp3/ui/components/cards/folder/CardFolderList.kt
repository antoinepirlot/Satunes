package earth.mp3.ui.components.cards.folder


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Folder

@Composable
fun CardFolderList(
    modifier: Modifier = Modifier,
    folderList: MutableList<Folder>,
    onClick: (folder: Folder) -> Unit
) {
    val lazyState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyState
    ) {
        items(folderList) { folder ->
            CardFolder(folderName = folder.getName(), onClick = { onClick(folder) })
        }
    }
}

@Composable
@Preview
fun CardFolderListPreview() {
    CardFolderList(folderList = mutableListOf(), onClick = {})
}