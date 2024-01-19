package earth.mp3.ui.components.cards.folder

import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CardFolder(
    modifier: Modifier = Modifier,
    folderName: String
) {
    TextButton(
        modifier = modifier,
        onClick = { /*TODO*/ } //redirect to the folder's view
    ) {
        Text(text = folderName)
    }
    Divider()
}

@Composable
@Preview
fun CardFolderPreview() {

    CardFolder(folderName = "Nom de dossier")
}