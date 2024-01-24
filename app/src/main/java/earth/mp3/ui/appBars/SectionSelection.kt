package earth.mp3.ui.appBars

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.Folder
import earth.mp3.ui.components.cards.menu.CardMenuList

@Composable
fun SectionSelection(
    modifier: Modifier = Modifier,
    startDestination: MutableState<String>,
    folderList: MutableList<Folder>,
) {
    val folderSelected = rememberSaveable { mutableStateOf(true) } // default section
    val artistsSelected = rememberSaveable { mutableStateOf(false) }
    val tracksSelected = rememberSaveable { mutableStateOf(false) }

    CardMenuList(
        modifier = modifier,
        startDestination = startDestination,
        folderSelected = folderSelected,
        artistsSelected = artistsSelected,
        tracksSelected = tracksSelected,
        resetFoldersToShow = {
            folderList.clear()
            for (folder in folderList) {
                folderList.add(folder)
            }
        }
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun SectionSelectionPreview() {
    SectionSelection(folderList = mutableListOf(), startDestination = mutableStateOf(""))
}