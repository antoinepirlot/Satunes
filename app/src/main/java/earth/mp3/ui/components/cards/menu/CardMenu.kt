package earth.mp3.ui.components.cards.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CardMenu(
    modifier: Modifier,
    menuTitle: String
) {
    TextButton(
        modifier = modifier,
        onClick = { /*TODO*/ }
    ) {
        Text(text = menuTitle)
    }
}

@Composable
@Preview
fun CardMenuPreview() {
    CardMenu(modifier = Modifier.fillMaxSize(), "Folder")
}