package earth.mp3.ui.components.cards

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun <T> CardList(
    modifier: Modifier = Modifier,
    objectList: List<T>,
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: (obj: T) -> Unit
) {
    val lazyState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyState
    ) {
        items(objectList) { obj ->
            Card(
                modifier = modifier,
                text = obj.toString(),
                imageVector = imageVector,
                contentDescription = contentDescription,
                onClick = { onClick(obj) }
            )
        }
    }
}

@Composable
@Preview
fun <T> CardListPreview() {
    CardList(
        objectList = listOf<T>(),
        imageVector = Icons.Filled.PlayArrow,
        onClick = {}
    )
}