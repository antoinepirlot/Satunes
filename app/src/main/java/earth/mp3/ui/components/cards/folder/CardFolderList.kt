package earth.mp3.ui.components.cards.folder

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CardFolderList(
    modifier: Modifier = Modifier,
) {
    val folderList = listOf(
        "folder",
        "test"
    )
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(folderList) { _: Int, folderName: String ->
            CardFolder(folderName = folderName)
        }
    }
}

@Composable
@Preview
fun CardFolderListPreview() {
    CardFolderList()
}