package earth.mp3.ui.components.cards.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CardMenu(
    modifier: Modifier,
    menuTitle: String,
    openedMenu: MutableState<MenuTitle>
) {

//    Button(
//        modifier = modifier,
//        onClick = { /*TODO*/ },
//        colors = ButtonDefaults.buttonColors(
//            containerColor = MaterialTheme.colorScheme.primary,
//            disabledContentColor = MaterialTheme.colorScheme.onSecondary
//        )
//    ) {
//        Text(
//            color = Color.Blue,
//            text = menuTitle
//        )
//    }
}

@Composable
@Preview
fun CardMenuPreview() {
    var openedMenu = rememberSaveable { mutableStateOf(MenuTitle.FOLDER) }
    CardMenu(modifier = Modifier.fillMaxSize(), menuTitle = "Folder", openedMenu = openedMenu)
}