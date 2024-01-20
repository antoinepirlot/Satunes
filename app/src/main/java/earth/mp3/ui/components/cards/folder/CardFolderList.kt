package earth.mp3.ui.components.cards.folder

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Folder

@Composable
fun CardFolderList(
    modifier: Modifier = Modifier,
    folderList: List<Folder>
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(folderList) { _: Int, folderData: Folder ->
            //TODO fix the multiple Music Folder showed, there's should only be one Music
            CardFolder(folderName = folderData.getName())
        }
    }
}

@Composable
@Preview
fun CardFolderListPreview() {
    CardFolderList(folderList = listOf())
}