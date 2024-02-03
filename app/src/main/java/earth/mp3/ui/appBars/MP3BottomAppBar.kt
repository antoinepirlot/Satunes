package earth.mp3.ui.appBars

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import earth.mp3.models.MenuTitle
import earth.mp3.router.Destination

@Composable
fun MP3BottomAppBar(
    modifier: Modifier = Modifier,
    startDestination: MutableState<String>
) {
    val menuTitleList = listOf(
        MenuTitle.FOLDER,
        MenuTitle.ARTISTS,
        MenuTitle.MUSIC
    )
    var selectedSection by remember { mutableStateOf(MenuTitle.FOLDER) }

    NavigationBar(
        modifier = modifier
    ) {
        menuTitleList.forEach { section: MenuTitle ->
            NavigationBarItem(
                label = { Text(text = section.name) },
                selected = selectedSection == section,
                onClick = {
                    selectedSection = section
                    when (selectedSection) {
                        MenuTitle.FOLDER -> {
                            startDestination.value = Destination.FOLDERS.link
                        }

                        MenuTitle.ARTISTS -> {
                            startDestination.value = Destination.ARTISTS.link
                        }

                        MenuTitle.MUSIC -> {
                            startDestination.value = Destination.MUSICS.link
                        }
                    }
                },
                icon = {
                    when (section) {
                        MenuTitle.FOLDER -> {
                            Icons.Rounded.Folder
                        }

                        MenuTitle.ARTISTS -> {
                            Icons.Rounded.AccountCircle
                        }

                        MenuTitle.MUSIC -> {
                            Icons.Rounded.Audiotrack
                        }
                    }
                }
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun MP3BottomAppBarPreview() {
    MP3BottomAppBar(startDestination = mutableStateOf(Destination.FOLDERS.link))
}