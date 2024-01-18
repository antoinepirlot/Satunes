package earth.mp3.ui.components.cards.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

    LazyRow(
        modifier = modifier
    ) {
        itemsIndexed(menuTitleList) { index: Int, menuTitle: String ->
            CardMenu(modifier = modifier, menuTitle)
        }
    }

}

@Composable
@Preview
fun CardListMenuPreview() {
    CardListMenu(modifier = Modifier.fillMaxSize())
}