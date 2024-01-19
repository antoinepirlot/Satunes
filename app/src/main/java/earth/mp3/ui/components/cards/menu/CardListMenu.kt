package earth.mp3.ui.components.cards.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import earth.mp3.R

@Composable
fun CardListMenu(
    modifier: Modifier
) {
    val menuTitleList = listOf(
        stringResource(id = R.string.folder),
        stringResource(id = R.string.artists),
        stringResource(id = R.string.tracks)
    )

    var openedMenu = rememberSaveable { mutableStateOf(MenuTitle.FOLDER) }
    var folderSelected = remember { mutableStateOf(true) } // default section
    var artistsSelected = remember { mutableStateOf(false) }
    var tracksSelected = remember { mutableStateOf(false) }

    LazyRow(
        modifier = modifier
    ) {
        itemsIndexed(menuTitleList) { index: Int, menuTitle: String ->
            //TODO get the right selected
            Tab(selected = selected.value, onClick = {
                if (menuTitle.equals(menuTitleList[0])) {
                    folderSelected.value = true
                    artistsSelected.value = false
                    tracksSelected.value = false
                } else if (menuTitle.equals(menuTitleList[1])) {
                    folderSelected.value = false
                    artistsSelected.value = true
                    tracksSelected.value = false
                } else {
                    folderSelected.value = false
                    artistsSelected.value = false
                    tracksSelected.value = true
                }

            }) {
                Column(
                    modifier
                        .padding(10.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier
                            .size(10.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(
                                color = if (selected.value) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.background
                            )
                    )
                    Text(
                        text = menuTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = modifier.align(Alignment.CenterHorizontally)
                    )

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