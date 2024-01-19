package earth.mp3.ui.components.cards.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.R

@Composable
fun CardListMenu(
    modifier: Modifier
) {
    val menuTitleList = listOf(
        MenuTitle.FOLDER,
        MenuTitle.ARTISTS,
        MenuTitle.TRACKS
    )

    val folderSelected = remember { mutableStateOf(true) } // default section
    val artistsSelected = remember { mutableStateOf(false) }
    val tracksSelected = remember { mutableStateOf(false) }

    LazyRow(
        modifier = modifier
    ) {
        itemsIndexed(menuTitleList) { _: Int, menuTitle: MenuTitle ->
            when (menuTitle) {
                MenuTitle.FOLDER -> {
                    CardMenu(
                        modifier = modifier,
                        menuTitle = stringResource(id = R.string.folder),
                        selected = folderSelected.value
                    ) {
                        folderSelected.value = true
                        artistsSelected.value = false
                        tracksSelected.value = false
                    }
                }

                MenuTitle.ARTISTS -> {
                    CardMenu(
                        modifier = modifier,
                        menuTitle = stringResource(id = R.string.artists),
                        selected = artistsSelected.value
                    ) {
                        folderSelected.value = false
                        artistsSelected.value = true
                        tracksSelected.value = false
                    }
                }

                MenuTitle.TRACKS -> {
                    CardMenu(
                        modifier = modifier,
                        menuTitle = stringResource(id = R.string.tracks),
                        selected = tracksSelected.value
                    ) {
                        folderSelected.value = false
                        artistsSelected.value = false
                        tracksSelected.value = true
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun CardListMenuPreview() {
    CardListMenu(modifier = Modifier.fillMaxSize())
}